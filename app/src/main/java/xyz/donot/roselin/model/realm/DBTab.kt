package xyz.donot.roselin.model.realm

import io.realm.RealmObject
import io.realm.annotations.RealmClass
import java.lang.Exception

const val HOME=0
const val LIST=1
const val MENTION=2
const val NOTIFICATION=3
fun ConvertToName(int: Int):String = when(int)
{
    HOME->"Home"
    LIST->"List"
    MENTION->"Reply"
    NOTIFICATION->"Notification"
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
    open  var searchWord:String?=null
}
