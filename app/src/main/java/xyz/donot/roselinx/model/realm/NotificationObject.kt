package xyz.donot.roselinx.model.realm


import io.realm.RealmObject
import java.util.*

const val NFAVORITE=100
const val NRETWEET=200


open  class NotificationObject : RealmObject() {
    open  var sourceUser: ByteArray?=null
    open  var status: ByteArray?=null
    open  var type: Int =0
    open var date:Date=Date()
}
