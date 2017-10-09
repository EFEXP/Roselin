package xyz.donot.roselinx.util

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import twitter4j.Status
import xyz.donot.roselinx.model.room.RoselinDatabase
import xyz.donot.roselinx.model.room.TwitterAccount
import xyz.klinker.android.drag_dismiss.DragDismissIntentBuilder
import java.io.*
import java.util.regex.Matcher
import java.util.regex.Pattern


fun <T : Serializable> T.getSerialized(): ByteArray = ByteArrayOutputStream().use {
    val out = ObjectOutputStream(it)
    out.writeObject(this)
    val bytes = it.toByteArray()
    out.close()
    return bytes
}

fun String.getMatcher(charSequence: CharSequence): Matcher {
    return Pattern.compile(this).matcher(charSequence)
}

fun <T> ByteArray.getDeserialized(): T {
    @Suppress("UNCHECKED_CAST")
    return ObjectInputStream(ByteArrayInputStream(this)).readObject() as T
}

fun PagerAdapter.findFragmentByPosition(viewPager: ViewPager, position: Int): Fragment {
    val fragment = instantiateItem(viewPager, position) as Fragment
    finishUpdate(viewPager)
    return fragment

}

fun getAccount(): TwitterAccount {
    return RoselinDatabase.getAllowedInstance().twitterAccountDao().getMainAccount(true)
}

fun haveToken(): Boolean {
    return RoselinDatabase.getAllowedInstance().twitterAccountDao().count() > 0
}

fun canPass(status: Status): Boolean {
    val userId = status.user.id
    val dao = RoselinDatabase.getInstance().muteFilterDao().getAllData()
    val mutedUserIds = dao.map { it.user }.filterNotNull().map { it.id }
    val containMutedWords = dao.map { it.text }.filterNotNull()

    if (mutedUserIds.contains(userId))
        return false
    containMutedWords.forEach {
        if (status.text.contains(it))
            return false
    }


    return true


}


fun Context.getDragdismiss(i: Intent): Intent {
    return DragDismissIntentBuilder(this)
            .setShowToolbar(false)
            .setDragElasticity(DragDismissIntentBuilder.DragElasticity.XXLARGE)
            .build(i)
}