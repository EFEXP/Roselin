package xyz.donot.quetzal.model.realm



import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class DBAccount : RealmObject() {
    @PrimaryKey open var id: Long = 0
    open  var isMain: Boolean = false
    open var user:ByteArray?=null
   open   var twitter: ByteArray? = null
}
