package xyz.donot.roselin.model.realm

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class DBTabData : RealmObject() {
    open  var name:String=""
    open  var accountId:Long=0L
    open  var listId:Long=0L
    open  var searchWord:String?=null
}
