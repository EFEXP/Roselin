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
            val instagramMatcher = INSTAGRAM_PATTERN.getMatcher(url.expandedURL)
            if (instagramMatcher.find()) {
                imageUrls.add(url.expandedURL + "media?size=l")
                continue
            }
            val youtubeMatcher = YOUTUBE_PATTERN.getMatcher(url.expandedURL)
            if (youtubeMatcher.find()) {
                imageUrls.add("http://i.ytimg.com/vi/" + youtubeMatcher.group(1) + "/hqdefault.jpg")
                continue
            }
            val niconicoMatcher = NICONICO_PATTERN.getMatcher(url.expandedURL)
            if (niconicoMatcher.find()) {
                val id = Integer.valueOf(niconicoMatcher.group(1))!!
                val host = id % 4 + 1
                imageUrls.add("http://tn-skr$host.smilevideo.jp/smile?i=$id.L")
                continue
            }
            val pixivMatcher = PIXIV_PATTERN.getMatcher(url.expandedURL)
            if (pixivMatcher.find()) {
                imageUrls.add("http://embed.pixiv.net/decorate.php?illust_id=" + pixivMatcher.group(1))
                continue
            }
            val twitpicMatcher = TWITPIC_PATTERN.getMatcher(url.expandedURL)
            if (twitpicMatcher.find()) {
                imageUrls.add("http://twitpic.com/show/full/" + twitpicMatcher.group(1))
                continue
            }
            val photozouMatcher = PHOTOZOU_PATTERN.getMatcher(url.expandedURL)
            if (photozouMatcher.find()) {
                imageUrls.add("http://photozou.jp/p/img/" + photozouMatcher.group(1))
                continue
            }
            val twippleMatcher = TWIPPLE_PATTERN.getMatcher(url.expandedURL)
            if (twippleMatcher.find()) {
                imageUrls.add("http://p.twpl.jp/show/orig/" + twippleMatcher.group(1))
                continue
            }
            val gyazoMatcher = GYAZO_PATTERN.getMatcher(url.expandedURL)
            if (gyazoMatcher.find()) {
                imageUrls.add("https://i.gyazo.com/" + gyazoMatcher.group(1) + ".png")
                continue
            }
            val imagesMatcher = IMAGES_PATTERN.getMatcher(url.expandedURL)
            if (imagesMatcher.find()) {
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
        return if (mediaEntities.isNotEmpty()) {
            val mediaEntity = mediaEntities[0]
            val url = mediaEntity.mediaURLHttps
            return if (url.matches(PIC_TWITTER_GIF.toRegex())) {
                true
            } else {
                mediaEntities.forEach {
                    if (it.videoVariants != null && it.videoVariants.isNotEmpty()) return true
                }
                false
            }
        } else false
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

