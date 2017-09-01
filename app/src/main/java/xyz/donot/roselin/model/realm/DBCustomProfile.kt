package xyz.donot.roselin.model.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

@RealmClass
open class DBCustomProfile : RealmObject() {
    @PrimaryKey open var id: Long = 0
    open  var customname: String?=null
    open  var birthday: Date?=null
    open  var memo: String?=null
}