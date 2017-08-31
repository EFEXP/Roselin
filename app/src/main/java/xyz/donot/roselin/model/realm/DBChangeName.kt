package xyz.donot.roselin.model.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class DBChangeName : RealmObject() {
    @PrimaryKey open var id: Long = 0
    open  var name: String?=null
}