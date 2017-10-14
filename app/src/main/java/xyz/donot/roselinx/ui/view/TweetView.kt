package xyz.donot.roselinx.ui.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.klinker.android.link_builder.LinkBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_classic_tweet.view.*
import twitter4j.Status
import twitter4j.User
import xyz.donot.roselinx.R
import xyz.donot.roselinx.ui.util.*
import xyz.donot.roselinx.ui.status.TweetCardPicAdapter
import xyz.donot.roselinx.ui.util.extraUtils.hide
import xyz.donot.roselinx.ui.util.extraUtils.onClick
import xyz.donot.roselinx.ui.util.extraUtils.show
import java.util.*
import kotlin.properties.Delegates

class TweetView(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attributeSet, defStyleAttr) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    var view by Delegates.notNull<View>()
    var iconClick: (User) -> Unit = {}
    var quoteClick: (Status) -> Unit = {}
    var favoriteClick: (Boolean, Long) -> Unit = { _, _ -> }
    var retweetClick: (Boolean, Long) -> Unit = { _, _ -> }
    var userNameClick: (String) -> Unit = {}
    var videoClick: (String, String) -> Unit = { _, _ -> }
    var pictureClick: (Int, ArrayList<String>) -> Unit = { _, _ -> }

    init {
        view = LayoutInflater.from(context).inflate(R.layout.item_classic_tweet, this)
    }

    fun setStatus(status: Status, item: Status, kichitsui: Boolean,customName:String?) {
        view.apply {
            if (status.isRetweet) {
                textview_is_retweet.text = "@${status.user.screenName}がリツイート"
                LinkBuilder.on(textview_is_retweet).addLinks(context.getRetweetMention()).build()
                //   textview_username.setTextColor(ContextCompat.getColor(context, R.color.retweet))
                //            textview_date.setTextColor(ContextCompat.getColor(context,R.color.retweet))
                //textview_screenname.setTextColor(ContextCompat.getColor(context,R.color.retweet))
                textview_is_retweet.show()
            } else {
                //   val t = TypedValue()
                //  val s = TypedValue()
                //  context.theme.resolveAttribute(android.R.attr.textColorPrimary, t, true)
                //  context.theme.resolveAttribute(android.R.attr.textColorSecondary, s, true)
                //  textview_username.setTextColor(ContextCompat.getColor(context, t.resourceId))
                //    textview_date.setTextColor(ContextCompat.getColor(context,s.resourceId))
                //     textview_screenname.setTextColor(ContextCompat.getColor(context,s.resourceId))
                textview_is_retweet.hide()
            }
            if (customName!=null)
                textview_username.text =customName
            else
                textview_username.text = item.user.name


                if (kichitsui) {
                    val array = context.resources.getStringArray(R.array.ARRAY_KITITSUI)
                    textview_text.text = array[Random().nextInt(array.count())]
                } else {
                    val text = getExpandedText(item)
                    if (text.codePointCount(0, text.length) == 0)
                        textview_text.hide()
                    else {
                        textview_text.text = text
                        textview_text.show()
                    }
            }
            textview_date.text = getRelativeTime(item.createdAt)
            textview_screenname.text = "@" + item.user.screenName
            textview_via.text = getClientName(item.source)
            tv_retweet.setText(item.retweetCount.toString())
            tv_favorite.setText(item.favoriteCount.toString())
            if (item.userMentionEntities.isNotEmpty()) {
                textview_to_reply.show()
                textview_to_reply.text = inReplyName(item)
                LinkBuilder.on(textview_to_reply).addLinks(context.getMentionLink()).build()
            } else {
                textview_to_reply.hide()
            }
            LinkBuilder.on(textview_text).addLinks(context.getTagURLMention()).build()
            //ふぁぼ済み
            val favdraw = if (item.isFavorited) R.drawable.wrap_favorite_pressed else R.drawable.wrap_favorite
            tv_favorite.setSrc(favdraw)
            //RT
            val rtdraw = if (status.isRetweeted) R.drawable.wrap_retweet_pressed else R.drawable.wrap_retweet
            tv_retweet.setSrc(rtdraw)
            //認証済み
            val vrdraw = if (item.user.isVerified) ResourcesCompat.getDrawable(context.resources, R.drawable.wraped_verify, null) else null
            textview_username.setCompoundDrawablesWithIntrinsicBounds(null, null, vrdraw, null)
            //鍵垢
            if (item.user.isProtected) {
                textview_username.setCompoundDrawablesWithIntrinsicBounds(null, null, ResourcesCompat.getDrawable(context.resources, R.drawable.wrap_lock, null), null)
            } else {
                textview_username.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
            //引用
            item.quotedStatus?.let {
                quote_tweet_holder.show()
                quoted_screenname.text = "@${item.quotedStatus.user.screenName}"
                quoted_text.text = item.quotedStatus.text
                quoted_name.text = item.quotedStatus.user.name
                Picasso.with(context).load(item.quotedStatus.user.biggerProfileImageURLHttps).fit().into(quoted_icon)
            } ?: quote_tweet_holder.hide()

            quote_tweet_holder.onClick { quoteClick(item.quotedStatus) }
            imageview_icon.onClick { iconClick(item.user) }
            tv_retweet.onClick { retweetClick(item.isRetweeted, status.id) }
            tv_favorite.onClick { favoriteClick(item.isFavorited, status.id) }
            //mediaType
            val statusMediaIds = item.images
            when {
                statusMediaIds.size >= 2 -> {
                    val mAdapter = TweetCardPicAdapter(statusMediaIds, item.hasVideo)
                    val manager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
                    val recycler = recyclerview_picture
                    recycler.apply {
                        //     if (onFlingListener == null) LinearSnapHelper().attachToRecyclerView(recycler)
                        adapter = mAdapter
                        layoutManager = manager
                        visibility = View.VISIBLE
                        hasFixedSize()
                    }
                    mAdapter.setOnItemClickListener { _, _, position_ ->
                        if (item.hasVideo) {
                            videoClick(item.getVideoURL()!!, item.mediaEntities[0].mediaURLHttps)
                        } else {
                            pictureClick(position_, item.images)
                        }
                    }
                }
                statusMediaIds.size == 1 -> {
                    val mAdapter = TweetCardPicAdapter(statusMediaIds, item.hasVideo)
                    val manager = LinearLayoutManager(context).apply {
                        orientation = LinearLayoutManager.HORIZONTAL
                    }
                    val recycler = recyclerview_picture
                    recycler.apply {
                        //     if (onFlingListener == null) LinearSnapHelper().attachToRecyclerView(recycler)
                        adapter = mAdapter
                        layoutManager = manager
                        visibility = View.VISIBLE
                        hasFixedSize()
                    }
                    mAdapter.setOnItemClickListener { _, _, position_ ->
                        if (item.hasVideo) {
                            videoClick(item.getVideoURL()!!, item.mediaEntities[0].mediaURLHttps)
                        } else {
                            pictureClick(position_, item.images)
                        }
                    }
                }
                else -> recyclerview_picture.hide()
            }
            Picasso.with(context).load(item.user.originalProfileImageURLHttps).fit().into(imageview_icon)

        }

    }


}
