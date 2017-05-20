package xyz.donot.roselin.util

import io.realm.Realm
import twitter4j.Twitter
import xyz.donot.roselin.model.realm.DBAccount
import xyz.donot.roselin.util.extraUtils.logi

import java.io.*


fun<T:Serializable> T.getSerialized():ByteArray{
    ByteArrayOutputStream().use {
        val out = ObjectOutputStream(it)
        out.writeObject(this)
        val bytes = it.toByteArray()
        out.close()
        return bytes
    }
}


fun<T> ByteArray.getDeserialized():T{
    @Suppress("UNCHECKED_CAST")
    return ObjectInputStream(ByteArrayInputStream(this)).readObject()as T
}


fun getTwitterInstance(): twitter4j.Twitter {
    Realm.getDefaultInstance().use {
        val ac= it.where(DBAccount::class.java).equalTo("isMain",true).findFirst()
        return ac.twitter?.getDeserialized<Twitter>()?:throw IllegalStateException()
    }

}

fun haveToken(): Boolean {
    Realm.getDefaultInstance().use {
        logi( "AddedAccounts","You have ${it.where(DBAccount::class.java).count()} accounts!")
        return  it.where(DBAccount::class.java).count()>0
    }
}
