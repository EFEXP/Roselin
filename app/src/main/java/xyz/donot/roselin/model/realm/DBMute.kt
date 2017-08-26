package xyz.donot.roselin.model.realm


import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass


@RealmClass
open  class DBMute : RealmObject() {
    @PrimaryKey open  var id: Long = 0L
    open var muteWord:String?=null
}