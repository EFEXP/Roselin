package xyz.donot.quetzal.model.realm

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class DBDraft : RealmObject() {
    open  var accountId:Long =0
    open  var text:String =""
    open  var replyToStatusId:Long =0
    open var replyToScreenName:String=""
}