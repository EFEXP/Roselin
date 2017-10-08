package xyz.donot.roselinx.model.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*


open class CustomProfileObject : RealmObject() {
    @PrimaryKey open var id: Long = 0
    open  var customname: String?=null
    open  var birthday: Date?=null
    open  var memo: String?=null
}