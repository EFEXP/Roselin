package xyz.donot.roselin.view.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.squareup.picasso.Picasso
import twitter4j.MediaEntity
import twitter4j.Status
import xyz.donot.roselin.R
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class StatusAdapter(val context: Context,list:List<Status>) : BaseQuickAdapter<Status, BaseViewHolder>(R.layout.item_tweet,list)
{


    override fun convert(helper: BaseViewHolder, status: Status) {
        val item= if (status.isRetweet){ status.retweetedStatus }else{ status }
        helper.apply {
            setText(R.id.textview_text, getExpandedText(item))
            setText(R.id.textview_username,item.user.name)
            setText(R.id.textview_screenname,"@"+item.user.screenName)
            setText(R.id.textview_via, getClientName(item.source))
            setText(R.id.textview_date, getRelativeTime(item.createdAt))
            setText(R.id.textview_count, "RT:${item.retweetCount} いいね:${item.favoriteCount}")
        }
        //mediaType
        val statusMediaIds=getImageUrls(item)
        if(statusMediaIds.isNotEmpty()){
            val mAdapter=TweetCardPicAdapter(context,statusMediaIds)
            val manager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
             helper.getView<RecyclerView>(R.id.recyclerview_picture).apply {
                adapter=mAdapter
                layoutManager=manager
                visibility = View.VISIBLE
                hasFixedSize()
            }

        }
        else{
            helper.getView<RecyclerView>(R.id.recyclerview_picture).visibility = View.GONE
        }

        Picasso.with(mContext).load(item.user.originalProfileImageURLHttps).into(helper.getView<ImageView>(R.id.imageview_icon))


    }



}


fun getClientName(source: String): String {
    val tokens = source.split("[<>]".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
    if (tokens.size > 1) {
        return tokens[2]
    } else {
        return tokens[0]
    }
}



fun getExpandedText(status: Status): String {
    var text = status.text
    for (url in status.urlEntities) {
        val p = Pattern.compile(url.url)
        val m = p.matcher(text)
        text = m.replaceAll(url.expandedURL)
    }
    return text
}



private val TWITPIC_PATTERN = Pattern.compile("^http://twitpic\\.com/(\\w+)$")
private val TWIPPLE_PATTERN = Pattern.compile("^http://p\\.twipple\\.jp/(\\w+)$")
private val INSTAGRAM_PATTERN = Pattern.compile("^https?://(?:www\\.)?instagram\\.com/p/([^/]+)/$")
private val PHOTOZOU_PATTERN = Pattern.compile("^http://photozou\\.jp/photo/show/\\d+/(\\d+)$")
private val IMAGES_PATTERN = Pattern.compile("^https?://.*\\.(png|gif|jpeg|jpg)$")
private val YOUTUBE_PATTERN = Pattern.compile("^https?://(?:www\\.youtube\\.com/watch\\?.*v=|youtu\\.be/)([\\w-]+)")
private val NICONICO_PATTERN = Pattern.compile("^http://(?:www\\.nicovideo\\.jp/watch|nico\\.ms)/sm(\\d+)$")
private val PIXIV_PATTERN = Pattern.compile("^http://www\\.pixiv\\.net/member_illust\\.php.*illust_id=(\\d+)")
private val GYAZO_PATTERN = Pattern.compile("^https?://gyazo\\.com/(\\w+)")
// pic.twitter.com
val PIC_TWITTER_GIF = "https?://pbs\\.twimg\\.com/tweet_video_thumb/[a-zA-Z0-9_\\-]+\\.png"
val PIC_TWITTER_GIF_URL_1 = "tweet_video_thumb"
val PIC_TWITTER_GIF_URL_2 = "png"
val PIC_TWITTER_GIF_REPLACE_1 = "tweet_video"
val PIC_TWITTER_GIF_REPLACE_2 = "mp4"

fun getVideoURL(mediaEntities: Array<MediaEntity>): String? {
    if (mediaEntities.isNotEmpty()) {
        val mediaEntity = mediaEntities[0]
        val url = mediaEntity.mediaURLHttps
        if (url.matches(PIC_TWITTER_GIF.toRegex())) {
            return url.replace(PIC_TWITTER_GIF_URL_1, PIC_TWITTER_GIF_REPLACE_1).replace(PIC_TWITTER_GIF_URL_2, PIC_TWITTER_GIF_REPLACE_2)
        }
        for (entity in mediaEntities) {
            val variants = entity.videoVariants
            if (variants != null && variants.isNotEmpty()) {
                val videoMap = TreeMap<Int, String>()
                val bitrateList = ArrayList<Int>()
                for (variant in variants) {
                    val contentType = variant.contentType
                    val videoUrl = variant.url
                    val bitrate = variant.bitrate
                    if (contentType == "video/mp4") {
                        bitrateList.add(bitrate)
                        videoMap.put(bitrate, videoUrl)
                    }
                }
                Collections.sort(bitrateList)
                return videoMap[bitrateList[bitrateList.size - 1]]
            }
        }
    }
    // MP4 Video

    return null
}

fun getImageUrls(status: Status): ArrayList<String> {
    val imageUrls = ArrayList<String>()
    for (url in status.urlEntities) {
        val twitpic_matcher = TWITPIC_PATTERN.matcher(url.expandedURL)
        if (twitpic_matcher.find()) {
            imageUrls.add("http://twitpic.com/show/full/" + twitpic_matcher.group(1))
            continue
        }
        val twipple_matcher = TWIPPLE_PATTERN.matcher(url.expandedURL)
        if (twipple_matcher.find()) {
            imageUrls.add("http://p.twpl.jp/show/orig/" + twipple_matcher.group(1))
            continue
        }
        val instagram_matcher = INSTAGRAM_PATTERN.matcher(url.expandedURL)
        if (instagram_matcher.find()) {
            imageUrls.add(url.expandedURL + "media?size=l")
            continue
        }
        val photozou_matcher = PHOTOZOU_PATTERN.matcher(url.expandedURL)
        if (photozou_matcher.find()) {
            imageUrls.add("http://photozou.jp/p/img/" + photozou_matcher.group(1))
            continue
        }
        val youtube_matcher = YOUTUBE_PATTERN.matcher(url.expandedURL)
        if (youtube_matcher.find()) {
            imageUrls.add("http://i.ytimg.com/vi/" + youtube_matcher.group(1) + "/hqdefault.jpg")
            continue
        }
        val niconico_matcher = NICONICO_PATTERN.matcher(url.expandedURL)
        if (niconico_matcher.find()) {
            val id = Integer.valueOf(niconico_matcher.group(1))!!
            val host = id % 4 + 1
            imageUrls.add("http://tn-skr$host.smilevideo.jp/smile?i=$id.L")
            continue
        }
        val pixiv_matcher = PIXIV_PATTERN.matcher(url.expandedURL)
        if (pixiv_matcher.find()) {
            imageUrls.add("http://embed.pixiv.net/decorate.php?illust_id=" + pixiv_matcher.group(1))
            continue
        }
        val gyazo_matcher = GYAZO_PATTERN.matcher(url.expandedURL)
        if (gyazo_matcher.find()) {
            imageUrls.add("https://i.gyazo.com/" + gyazo_matcher.group(1) + ".png")
            continue
        }
        val images_matcher = IMAGES_PATTERN.matcher(url.expandedURL)
        if (images_matcher.find()) {
            imageUrls.add(url.expandedURL)
        }
    }

    if (status.mediaEntities.isNotEmpty()) {
        status.mediaEntities.mapTo(imageUrls) { it.mediaURL }
    } else {
        status.mediaEntities.mapTo(imageUrls) { it.mediaURL }
    }

    return imageUrls
}
fun getRelativeTime(create: Date): String {
    val datetime1 = System.currentTimeMillis()
    val datetime2 = create.time
    val Difference = datetime1 - datetime2
    return if (Difference < 60000L) {
        "%d秒前".format(TimeUnit.MILLISECONDS.toSeconds(Difference))
    } else if (Difference < 3600000L) {
        "%d分前".format(TimeUnit.MILLISECONDS.toMinutes(Difference))
    } else if (Difference < 86400000L) {
        "%d時間前".format(TimeUnit.MILLISECONDS.toHours(Difference))
    } else {
        "%d日前".format(TimeUnit.MILLISECONDS.toDays(Difference))
    }
}