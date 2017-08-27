package xyz.donot.roselin.model.realm


import io.realm.RealmObject
import io.realm.annotations.RealmClass
import java.util.*

const val NFAVORITE=100
const val NRETWEET=200

@RealmClass
open  class DBNotification : RealmObject() {
    open  var sourceUser: ByteArray=kotlin.ByteArray(0)
    open  var status: ByteArray=kotlin.ByteArray(0)
    open  var type: Int =0
    open var date:Date=Date()
}
