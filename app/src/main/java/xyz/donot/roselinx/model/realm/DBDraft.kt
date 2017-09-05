package xyz.donot.roselinx.model.realm


import io.realm.RealmObject
import io.realm.annotations.Required


open class DBDraft : RealmObject() {
    open  var accountId:Long =0
	@Required
    open  var text:String =""
    open  var replyToStatusId:Long =0
    open var replyToScreenName:String=""
}
