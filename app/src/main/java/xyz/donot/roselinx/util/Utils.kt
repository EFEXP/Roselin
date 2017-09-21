package xyz.donot.roselinx.util

import io.realm.Realm
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.User
import xyz.donot.roselinx.model.realm.AccountObject
import xyz.donot.roselinx.model.realm.MuteObject
import xyz.donot.roselinx.util.extraUtils.logd
import java.io.*


fun <T : Serializable> T.getSerialized(): ByteArray = ByteArrayOutputStream().use {
    val out = ObjectOutputStream(it)
    out.writeObject(this)
    val bytes = it.toByteArray()
    out.close()
    return bytes
}


fun <T> ByteArray.getDeserialized(): T {
    @Suppress("UNCHECKED_CAST")
    return ObjectInputStream(ByteArrayInputStream(this)).readObject() as T
}

fun getTwitterInstance(): twitter4j.Twitter = Realm.getDefaultInstance().use {
    val ac = it.where(AccountObject::class.java).equalTo("isMain", true).findFirst()
    return ac?.twitter?.getDeserialized<Twitter>() ?: throw IllegalStateException()
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

fun canPass(status: Status): Boolean {
    val userId = status.user.id
    val text = status.text
    Realm.getDefaultInstance().use { realm ->
        if (realm.where(MuteObject::class.java).equalTo("id", userId).count() > 0) {
            return false
        }
        if (realm.where(MuteObject::class.java).equalTo("text", text).count() > 0) {
            return false
        }
    }
    return true

}

