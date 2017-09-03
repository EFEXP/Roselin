package xyz.donot.roselin.model.realm


import io.realm.RealmObject



open  class DBMute : RealmObject() {
    open  var id: Long = 0L
    open var user:ByteArray?=null
    open var text:String?=null
}