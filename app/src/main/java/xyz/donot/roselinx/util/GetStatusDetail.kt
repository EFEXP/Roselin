package xyz.donot.roselinx.util

import twitter4j.Status
import java.text.BreakIterator
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


fun getRelativeTime(create: Date): String {
    val datetime1 = System.currentTimeMillis()
    val datetime2 = create.time
    val difference = datetime1 - datetime2
    return when {
        difference < 10000L -> "いま".format(TimeUnit.MILLISECONDS.toSeconds(difference))
        difference < 60000L -> "%d秒前".format(TimeUnit.MILLISECONDS.toSeconds(difference))
        difference < 3600000L -> "%d分前".format(TimeUnit.MILLISECONDS.toMinutes(difference))
        difference < 86400000L -> "%d時間前".format(TimeUnit.MILLISECONDS.toHours(difference))
        else -> "%d日前".format(TimeUnit.MILLISECONDS.toDays(difference))
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

fun inReplyName(status: Status): CharSequence {
    val text=StringBuilder()
    for (screen in status.userMentionEntities){
        text.append("@${screen.screenName} ")
    }
    text.append("へのリプライ")
   return text
}

fun getExpandedText(status: Status): String {
    var text:String = status.text
    if (status.displayTextRangeStart>=0&&status.displayTextRangeEnd>=0) {
        text = emojiSubString(text, status.displayTextRangeStart, status.displayTextRangeEnd)
    }
    for   (url in status.urlEntities) {
        text =  Pattern.compile(url.url).matcher(text).replaceAll(url.expandedURL)
    }

   /* for (url in status.urlEntities) {
        text =  Pattern.compile(url.url).matcher(text).replaceAll(url.displayURL)
    }
    for (url in status.mediaEntities) {
        text = Pattern.compile(url.url).matcher(text).replaceAll("")
    }
    for (screen in status.userMentionEntities) {
        text = Pattern.compile("@"+screen.screenName).matcher(text).replaceAll("")
    }*/
    return text
}

private fun emojiSubString(target:String, startIndex: Int, endIndex: Int): String {
    val bi = BreakIterator.getCharacterInstance()
    bi.setText(target)
    val sb = StringBuffer()

    // 繰り返し用開始位置
    var start = bi.first()
    // 繰り返し用終了位置
    var end :Int
    // 文字数
    var count = 0
    // 文字の最後まで繰り返し
    while (bi.next() != BreakIterator.DONE) {
        end = bi.current()
        // 文字数カウントアップ
        count++
        // 引数の開始位置と終了位置の間に文字を取得する
        if (count >= startIndex + 1 && count <= endIndex) {
            sb.append(target.substring(start, end))
        }
        start = end
    }
    return sb.toString()
}