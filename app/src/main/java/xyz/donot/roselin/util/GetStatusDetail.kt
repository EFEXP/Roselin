package xyz.donot.roselin.util

import twitter4j.Status
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


fun getRelativeTime(create: Date): String {
    val datetime1 = System.currentTimeMillis()
    val datetime2 = create.time
    val Difference = datetime1 - datetime2
    return when {
        Difference < 10000L -> "いま".format(TimeUnit.MILLISECONDS.toSeconds(Difference))
        Difference < 60000L -> "%d秒前".format(TimeUnit.MILLISECONDS.toSeconds(Difference))
        Difference < 3600000L -> "%d分前".format(TimeUnit.MILLISECONDS.toMinutes(Difference))
        Difference < 86400000L -> "%d時間前".format(TimeUnit.MILLISECONDS.toHours(Difference))
        else -> "%d日前".format(TimeUnit.MILLISECONDS.toDays(Difference))
    }
}
fun getClientName(source: String): String {
    val tokens = source.split("[<>]".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
    return if (tokens.size > 1) {
        tokens[2]
    } else {
        tokens[0]
    }
}
fun getExpandedText(status: Status): String {
    var text = status.text
    if (status.urlEntities.isNotEmpty())
    for (url in status.urlEntities) {
        val p = Pattern.compile(url.url)
        val m = p.matcher(text)
        text = m.replaceAll(url.expandedURL)
    }
    if (status.mediaEntities.isNotEmpty()) {
        for (it in   status.mediaEntities) {
            val p = Pattern.compile(it.url)
            val m = p.matcher(text)
            text = m.replaceAll("")
        }
    }
    return text
}