package xyz.donot.roselinx.util

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import io.realm.Realm
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.User
import xyz.donot.roselinx.model.realm.AccountObject
import xyz.donot.roselinx.model.room.RoselinDatabase
import xyz.donot.roselinx.util.extraUtils.logd
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

fun getTwitterInstance(): twitter4j.Twitter = Realm.getDefaultInstance().use {
    val ac = it.where(AccountObject::class.java).equalTo("isMain", true).findFirst()
    return ac?.twitter?.getDeserialized<Twitter>() ?: throw IllegalStateException()
}

fun getAccountObject(): AccountObject = Realm.getDefaultInstance().use {
    val ac = it.where(AccountObject::class.java).equalTo("isMain", true).findFirst()
    return ac ?: throw IllegalStateException()
}

fun PagerAdapter.findFragmentByPosition(viewPager: ViewPager, position: Int): Fragment {
    val fragment = instantiateItem(viewPager, position) as Fragment
    finishUpdate(viewPager)
    return fragment

}

fun getMyScreenName(): String = Realm.getDefaultInstance().use {
    val b = it.where(AccountObject::class.java).equalTo("isMain", true).findFirst()?.user!!.getDeserialized<User>()
    return b.screenName
}

fun getMyId(): Long = Realm.getDefaultInstance().use {
    return it.where(AccountObject::class.java).equalTo("isMain", true).findFirst()!!.id
}

fun haveToken(): Boolean = Realm.getDefaultInstance().use {
    logd("AddedAccounts", "You have ${it.where(AccountObject::class.java).count()} accounts!")
    return it.where(AccountObject::class.java).count() > 0
}

fun canPass(status: Status, context: Context): Boolean {
    val userId = status.user.id
    val dao= RoselinDatabase.getInstance(context).muteFilterDao().getAllData()
    val mutedUserIds=dao.map { it.user}.filterNotNull().map {  it.id }
    val mutedWords=dao.map {it.text}.filterNotNull().filter { status.text.contains(it) }

    if (mutedUserIds.contains(userId))
        return false
    if (mutedWords.isNotEmpty())
        return false

        return true

}


fun Context.getDragdismiss(i: Intent): Intent {
    return DragDismissIntentBuilder(this)
            .setShowToolbar(false)
            .setDragElasticity(DragDismissIntentBuilder.DragElasticity.XXLARGE)
            .build(i)
}