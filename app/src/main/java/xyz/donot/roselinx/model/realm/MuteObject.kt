package xyz.donot.roselinx.model.realm


import io.realm.RealmObject


open  class MuteObject : RealmObject() {
    open  var id: Long = 0L
    open var user:ByteArray?=null
    open var text:String?=null
    open var kichitsui:Boolean=false
}
