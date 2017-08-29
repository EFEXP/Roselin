package xyz.donot.roselin.model.realm

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import io.realm.RealmObject
import io.realm.annotations.RealmClass
import xyz.donot.roselin.R
import java.lang.Exception


fun Context.typeToIcon(type:Int): Drawable {
    val d= when(type){
        HOME-> R.drawable.ic_home
        MENTION-> R.drawable.ic_reply
        SEARCH-> R.drawable.ic_search
        LIST-> R.drawable.ic_view_list
        NOTIFICATION-> R.drawable.ic_notifications
        TREND-> R.drawable.ic_trending
        else->throw IllegalStateException()
    }
      val d2= DrawableCompat.wrap(ContextCompat.getDrawable(this,d))
     DrawableCompat.setTint(d2, ContextCompat.getColor(this,android.R.color.white))
     DrawableCompat.setTintMode(d2, PorterDuff.Mode.SRC_IN)
      return d2
}
const val HOME=0
const val LIST=1
const val MENTION=2
const val NOTIFICATION=3
const val TREND=4
const val SEARCH=5
fun ConvertToName(int: Int):String = when(int)
{
    HOME->"Home"
    LIST->"List"
    MENTION->"Reply"
    NOTIFICATION->"Notification"
    SEARCH->"Search"
    TREND->"Trend"
    else->throw Exception()
}
fun ConvertToSimpleName(int: Int):String = when(int)
{
    HOME->"Home"
    LIST->"List"
    MENTION->"Reply"
    NOTIFICATION->"Notice"
    SEARCH->"Search"
    TREND->"Trend"
    else->throw Exception()
}
@RealmClass
open class DBTabData : RealmObject() {
    open  var type:Int=0
    open  var screenName:String?=null
    open  var accountId:Long=0L
    open  var order:Int=0
    open  var listId:Long=0L
    open  var listName:String?=null
    open  var searchQuery:ByteArray?=null
    open  var searchWord:String?=null
}
