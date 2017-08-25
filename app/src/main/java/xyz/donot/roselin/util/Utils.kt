package xyz.donot.roselin.util

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import io.realm.Realm
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.User
import twitter4j.conf.ConfigurationBuilder
import xyz.donot.roselin.R
import xyz.donot.roselin.model.realm.DBAccount
import xyz.donot.roselin.util.extraUtils.logd
import xyz.donot.roselin.util.extraUtils.mainThread
import java.io.*




fun<T:Serializable> T.getSerialized():ByteArray = ByteArrayOutputStream().use {
    val out = ObjectOutputStream(it)
    out.writeObject(this)
    val bytes = it.toByteArray()
    out.close()
    return bytes
}

fun<U,T:BaseViewHolder> BaseQuickAdapter<U,T>.remove(item:U){
    val int=   data.indexOf(item)
   remove(int)
}
fun<U,T:BaseViewHolder> BaseQuickAdapter<U,T>.replace(replacedItem: U,replaceItem: U){
    val int=  data.indexOf(replacedItem)
    mainThread {
        data.removeAt(int)
        data.add(int,replaceItem)
        notifyItemChanged(int)
    }
}


fun<T> ByteArray.getDeserialized():T{
    @Suppress("UNCHECKED_CAST")
    return ObjectInputStream(ByteArrayInputStream(this)).readObject()as T
}


fun getTwitterInstance(): twitter4j.Twitter = Realm.getDefaultInstance().use {
    val ac= it.where(DBAccount::class.java).equalTo("isMain",true).findFirst()
    return ac.twitter?.getDeserialized<Twitter>()?:throw IllegalStateException()
}
fun getOfficialInstance(context: Context): twitter4j.Twitter {
    val builder= ConfigurationBuilder()
    builder.setOAuthConsumerKey(context.getString(R.string.twitter_consumer_key))
    builder.setOAuthConsumerSecret(context.getString(R.string.twitter_consumer_secret))
    return TwitterFactory(builder.build()).instance
}


fun getMyScreenName(): String = Realm.getDefaultInstance().use {
  val b=  it.where(DBAccount::class.java).equalTo("isMain",true).findFirst().user!!.getDeserialized<User>()
    return  b.screenName
}
fun getMyId(): Long = Realm.getDefaultInstance().use {
    return  it.where(DBAccount::class.java).equalTo("isMain",true).findFirst().id
}
fun haveToken(): Boolean = Realm.getDefaultInstance().use {
    logd( "AddedAccounts","You have ${it.where(DBAccount::class.java).count()} accounts!")
    return  it.where(DBAccount::class.java).count()>0
}

