package xyz.donot.roselin.view.adapter

import android.app.Activity
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.view.View
import android.view.ViewGroup
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.LinkBuilder
import com.squareup.picasso.Picasso
import io.realm.Realm
import kotlinx.android.synthetic.main.item_classic_tweet.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Status
import xyz.donot.roselin.R
import xyz.donot.roselin.model.realm.DBCustomProfile
import xyz.donot.roselin.util.*
import xyz.donot.roselin.util.extraUtils.*
import xyz.donot.roselin.view.activity.PictureActivity
import xyz.donot.roselin.view.activity.TwitterDetailActivity
import xyz.donot.roselin.view.activity.UserActivity
import xyz.donot.roselin.view.activity.VideoActivity
import xyz.donot.roselin.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselin.view.custom.MyViewHolder
import kotlin.properties.Delegates


class StatusAdapter : MyBaseRecyclerAdapter<Status, MyViewHolder>(R.layout.item_classic_tweet) {
	var realm:Realm by Delegates.notNull()
	override fun convert(helper: MyViewHolder, status: Status, position: Int) {
		realm = Realm.getDefaultInstance()
		helper.getView<ViewGroup>(R.id.item_tweet_root).apply {
			val item = if (status.isRetweet) {
				textview_is_retweet.text = "@${status.user.screenName}がリツイート"
				LinkBuilder.on(textview_is_retweet).addLinks(mContext.getMentionLink()).build()
				textview_is_retweet.show()
				status.retweetedStatus
			} else {
				textview_is_retweet.hide()
				status
			}
			//Link
			val mentionsLink = arrayListOf(
					Link(Regex.MENTION_PATTERN)
							.setUnderlined(false)
							.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent))
							.setOnClickListener {

							})
			//テキスト関係
			val query = realm.where(DBCustomProfile::class.java).equalTo("id", item.user.id)
			if (query.count() > 0) {
				textview_username.text = query.findFirst()?.customname }
			else
				textview_username.text = item.user.name
			textview_date.text = getRelativeTime(item.createdAt)
			textview_text.text = getExpandedText(item)
			textview_screenname.text = "@" + item.user.screenName
			textview_via.text = getClientName(item.source)
			tv_retweet.text = item.retweetCount.toString()
			tv_favorite.text = item.favoriteCount.toString()
			item.inReplyToScreenName?.let {
				textview_to_reply.show()
				textview_to_reply.text = "@${item.inReplyToScreenName}へのリプライ"
				LinkBuilder.on(textview_to_reply).addLinks(mentionsLink).build()
			} ?: textview_to_reply.hide()
			LinkBuilder.on(textview_text).addLinks(mContext.getTagURLMention()).build()
			//ふぁぼ済み
			val favdraw = if (item.isFavorited) R.drawable.wrap_favorite_pressed else R.drawable.wrap_favorite
			tv_favorite.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(mContext.resources, favdraw, null), null, null, null)
			//RT
			val rtdraw = if (item.isRetweeted) R.drawable.wrap_retweet_pressed else R.drawable.wrap_retweet
			tv_retweet.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(mContext.resources, rtdraw, null), null, null, null)
			//認証済み
			val vrdraw = if (item.user.isVerified || item.user.screenName == "JlowoIL") ResourcesCompat.getDrawable(mContext.resources, R.drawable.wraped_verify, null) else null
			textview_username.setCompoundDrawablesWithIntrinsicBounds(null, null, vrdraw, null)
			//鍵垢
			if (item.user.isProtected) {
				textview_via.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(mContext.resources, R.drawable.wrap_lock, null), null, null, null)
			} else {
				textview_via.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
			}
//引用
			item.quotedStatus?.let {
				quote_tweet_holder.show()
				quoted_screenname.text = "@" + item.quotedStatus.user.screenName
				quoted_text.text = item.quotedStatus.text
				quoted_name.text = item.quotedStatus.user.name
				Picasso.with(mContext).load(item.quotedStatus.user.biggerProfileImageURLHttps).resize(100, 100).into(quoted_icon)
			} ?: quote_tweet_holder.hide()

			//Listener
			quote_tweet_holder.onClick {
				(mContext as Activity).start<TwitterDetailActivity>(Bundle { putSerializable("Status", item.quotedStatus) })
			}
			imageview_icon.setOnClickListener {
				val intent = mContext.intent<UserActivity>()
				intent.putExtra("user_id", item.user.id)
				mContext.startActivity(intent)
			}
			tv_favorite.setOnClickListener {
				if (item.isFavorited) {
					launch(UI) {
						try {
							val result = async(CommonPool) { getTwitterInstance().destroyFavorite(status.id) }.await()
							replace(status, result)
						} catch (e: Exception) {
							mContext.twitterExceptionToast(e)
						}
					}
				} else {
					launch(UI) {
						try {
							val result = async(CommonPool) { getTwitterInstance().createFavorite(status.id) }.await()
							replace(status, result)
						} catch (e: Exception) {
							mContext.twitterExceptionToast(e)
						}
					}
				}
			}
			tv_retweet.setOnClickListener {
				if (!status.isRetweeted) {
					launch(UI) {
						try {
							val result = async(CommonPool) { getTwitterInstance().retweetStatus(status.id) }.await()
							replace(status, result)
							mContext.toast("RTしました")
						} catch (e: Exception) {
							mContext.twitterExceptionToast(e)
						}

					}
				}
			}
			//mediaType
			val statusMediaIds = item.images
			if (statusMediaIds.isNotEmpty()) {
				val mAdapter = TweetCardPicAdapter(statusMediaIds, item.hasVideo)
				val manager = LinearLayoutManager(mContext).apply {
					orientation = LinearLayoutManager.HORIZONTAL
				}
				val recycler = recyclerview_picture
				recycler.apply {
					if (onFlingListener == null) LinearSnapHelper().attachToRecyclerView(recycler)
					adapter = mAdapter
					layoutManager = manager
					visibility = View.VISIBLE
					hasFixedSize()
				}
				mAdapter.setOnItemClickListener { _, _, position_ ->
					if (item.hasVideo) {
						mContext.startActivity(Intent(mContext, VideoActivity::class.java).putExtra("video_url", item.getVideoURL()))
					} else {
						(mContext as Activity).start<PictureActivity>(Bundle {
							putInt("start_page", position_)
							putStringArrayList("picture_urls", item.images)
						})
					}
				}
			} else {
				recyclerview_picture.hide()
			}
			Picasso.with(mContext).load(item.user.originalProfileImageURLHttps).into(imageview_icon)


		}


		//    val array= mContext.resources.getStringArray(R.array.ARRAY_KITITSUI)
		//      setText(R.id.textview_text,array[Random().nextInt(array.count())])

	}
}

