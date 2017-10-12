package xyz.donot.roselinx.ui.util

import twitter4j.Status
import java.util.*

const private val TWITPIC_PATTERN = "^http://twitpic\\.com/(\\w+)$"
const private val TWIPPLE_PATTERN = "^http://p\\.twipple\\.jp/(\\w+)$"
const private val INSTAGRAM_PATTERN = "^https?://(?:www\\.)?instagram\\.com/p/([^/]+)/$"
const private val PHOTOZOU_PATTERN = "^http://photozou\\.jp/photo/show/\\d+/(\\d+)$"
const private val IMAGES_PATTERN = "^https?://.*\\.(png|gif|jpeg|jpg)$"
const private val YOUTUBE_PATTERN ="^https?://(?:www\\.youtube\\.com/watch\\?.*v=|youtu\\.be/)([\\w-]+)"
const private val NICONICO_PATTERN = "^http://(?:www\\.nicovideo\\.jp/watch|nico\\.ms)/sm(\\d+)$"
const private val PIXIV_PATTERN = "^http://www\\.pixiv\\.net/member_illust\\.php.*illust_id=(\\d+)"
const private val GYAZO_PATTERN ="^https?://gyazo\\.com/(\\w+)"
const private val PIC_TWITTER_GIF = "https?://pbs\\.twimg\\.com/tweet_video_thumb/[a-zA-Z0-9_\\-]+\\.png"
const private val PIC_TWITTER_GIF_URL_1 = "tweet_video_thumb"
const private val PIC_TWITTER_GIF_URL_2 = "png"
const private val PIC_TWITTER_GIF_REPLACE_1 = "tweet_video"
const private val PIC_TWITTER_GIF_REPLACE_2 = "mp4"



val Status.images: ArrayList<String>
    get() {
        val imageUrls = java.util.ArrayList<String>()
        for (url in urlEntities) {
            val twitpic_matcher = TWITPIC_PATTERN.getMatcher(url.expandedURL)
            if (twitpic_matcher.find()) {
                imageUrls.add("http://twitpic.com/show/full/" + twitpic_matcher.group(1))
                continue
            }
            val twipple_matcher = TWIPPLE_PATTERN.getMatcher(url.expandedURL)
            if (twipple_matcher.find()) {
                imageUrls.add("http://p.twpl.jp/show/orig/" + twipple_matcher.group(1))
                continue
            }
            val instagram_matcher = INSTAGRAM_PATTERN.getMatcher(url.expandedURL)
            if (instagram_matcher.find()) {
                imageUrls.add(url.expandedURL + "media?size=l")
                continue
            }
            val photozou_matcher = PHOTOZOU_PATTERN.getMatcher(url.expandedURL)
            if (photozou_matcher.find()) {
                imageUrls.add("http://photozou.jp/p/img/" + photozou_matcher.group(1))
                continue
            }
            val youtube_matcher = YOUTUBE_PATTERN.getMatcher(url.expandedURL)
            if (youtube_matcher.find()) {
                imageUrls.add("http://i.ytimg.com/vi/" + youtube_matcher.group(1) + "/hqdefault.jpg")
                continue
            }
            val niconico_matcher = NICONICO_PATTERN.getMatcher(url.expandedURL)
            if (niconico_matcher.find()) {
                val id = Integer.valueOf(niconico_matcher.group(1))!!
                val host = id % 4 + 1
                imageUrls.add("http://tn-skr$host.smilevideo.jp/smile?i=$id.L")
                continue
            }
            val pixiv_matcher = PIXIV_PATTERN.getMatcher(url.expandedURL)
            if (pixiv_matcher.find()) {
                imageUrls.add("http://embed.pixiv.net/decorate.php?illust_id=" + pixiv_matcher.group(1))
                continue
            }
            val gyazo_matcher = GYAZO_PATTERN.getMatcher(url.expandedURL)
            if (gyazo_matcher.find()) {
                imageUrls.add("https://i.gyazo.com/" + gyazo_matcher.group(1) + ".png")
                continue
            }
            val images_matcher = IMAGES_PATTERN.getMatcher(url.expandedURL)
            if (images_matcher.find()) {
                imageUrls.add(url.expandedURL)
            }
        }

        if (mediaEntities.isNotEmpty()) {
            mediaEntities.mapTo(imageUrls) { it.mediaURLHttps }
        } else {
            mediaEntities.mapTo(imageUrls) { it.mediaURLHttps }
        }

        return imageUrls
    }

val Status.hasVideo: Boolean
    get() {
        if (mediaEntities.isNotEmpty()) {
            val mediaEntity = mediaEntities[0]
            val url = mediaEntity.mediaURLHttps
            if (url.matches(PIC_TWITTER_GIF.toRegex())) {
                return true
            } else {
                mediaEntities.forEach {
                    if (it.videoVariants != null && it.videoVariants.isNotEmpty()) return true
                }
                return false
            }
        } else return false
    }

fun Status.getVideoURL(): String? {
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
    return null
}

