package xyz.donot.roselinx.view.adapter

import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.View
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
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.realm.DBCustomProfile
import xyz.donot.roselinx.util.*
import xyz.donot.roselinx.util.extraUtils.*
import xyz.donot.roselinx.view.activity.PictureActivity
import xyz.donot.roselinx.view.activity.TwitterDetailActivity
import xyz.donot.roselinx.view.activity.UserActivity
import xyz.donot.roselinx.view.activity.VideoActivity

class SimpleStatusAdapter(data: ArrayList<Status>, val context: Context) : SimpleBaseAdapter<Status>(data,R.layout.item_classic_tweet) {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val status = data[position]
        holder.itemView.apply {
            val item = if (status.isRetweet) {
                textview_is_retweet.text = "@${status.user.screenName}がリツイート"
                LinkBuilder.on(textview_is_retweet).addLinks(context.getMentionLink()).build()
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
                            .setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                            .setOnClickListener {
                                (context as Application).startActivity(context.newIntent<UserActivity>(Bundle { putString("screen_name", it.replace("@", "")) }))
                            })
            //テキスト関係
            Realm.getDefaultInstance().use { realm ->
                val query = realm.where(DBCustomProfile::class.java).equalTo("id", item.user.id)
                if (query.count() > 0) {
                    textview_username.text = query.findFirst()?.customname
                } else
                    textview_username.text = item.user.name
            }
            textview_date.text = getRelativeTime(item.createdAt)
            textview_text.text = getExpandedText(item)
            textview_screenname.text = "@" + item.user.screenName
            textview_via.text = getClientName(item.source)
            tv_retweet.text = item.retweetCount.toString()
            tv_favorite.text = item.favoriteCount.toString()
            if (item.userMentionEntities.isNotEmpty()) {
                textview_to_reply.show()
                textview_to_reply.text = inReplyName(item)
                LinkBuilder.on(textview_to_reply).addLinks(mentionsLink).build()
            }
            else{textview_to_reply.hide()}

            LinkBuilder.on(textview_text).addLinks(context.getTagURLMention()).build()
            //ふぁぼ済み
            val favdraw = if (item.isFavorited) R.drawable.wrap_favorite_pressed else R.drawable.wrap_favorite
            tv_favorite.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(context.resources, favdraw, null), null, null, null)
            //RT
            val rtdraw = if (status.isRetweeted) R.drawable.wrap_retweet_pressed else R.drawable.wrap_retweet
            tv_retweet.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(context.resources, rtdraw, null), null, null, null)
            //認証済み
            val vrdraw = if (item.user.isVerified) ResourcesCompat.getDrawable(context.resources, R.drawable.wraped_verify, null) else null
            textview_username.setCompoundDrawablesWithIntrinsicBounds(null, null, vrdraw, null)
            //鍵垢
            if (item.user.isProtected) {
                textview_via.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(context.resources, R.drawable.wrap_lock, null), null, null, null)
            } else {
                textview_via.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
//引用
            item.quotedStatus?.let {
                quote_tweet_holder.show()
                quoted_screenname.text = "@${item.quotedStatus.user.screenName}"
                quoted_text.text = item.quotedStatus.text
                quoted_name.text = item.quotedStatus.user.name
                Picasso.with(context).load(item.quotedStatus.user.biggerProfileImageURLHttps).fit().into(quoted_icon)
            } ?: quote_tweet_holder.hide()

            //Listener
            quote_tweet_holder.onClick {
                (context as Application).start<TwitterDetailActivity>(Bundle { putSerializable("Status", item.quotedStatus) })
            }
            imageview_icon.setOnClickListener {
                val intent = context.intent<UserActivity>()
                intent.putExtra("user_id", item.user.id)
                context.startActivity(intent)
            }
            tv_favorite.setOnClickListener {
                if (item.isFavorited) {
                    launch(UI) {
                        try {
                            val result = async(CommonPool) { getTwitterInstance().destroyFavorite(status.id) }.await()
                            setData(status,result)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    launch(UI) {
                        try {
                            val result = async(CommonPool) { getTwitterInstance().createFavorite(status.id) }.await()
                            setData(status,result)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            tv_retweet.setOnClickListener {
                if (!status.isRetweeted) {
                    launch(UI) {
                        try {
                            val result = async(CommonPool) { getTwitterInstance().retweetStatus(status.id) }.await()
                            setData(status,result)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            //mediaType
            val statusMediaIds = item.images
            if (statusMediaIds.isNotEmpty()) {
                val mAdapter = TweetCardPicAdapter(statusMediaIds, item.hasVideo)
                val manager = LinearLayoutManager(context).apply {
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
                        context.startActivity(Intent(context, VideoActivity::class.java).apply {
                            putExtra("video_url", item.getVideoURL())
                            putExtra("thumbUrl",item.mediaEntities[0].mediaURL)
                        }


                        )
                    } else {
                        (context as Application).start<PictureActivity>(Bundle {
                            putInt("start_page", position_)
                            putStringArrayList("picture_urls", item.images)
                        })
                    }
                }
            } else {
                recyclerview_picture.hide()
            }
            Picasso.with(context).load(item.user.originalProfileImageURLHttps).fit().into(imageview_icon)
        }


    }



}


