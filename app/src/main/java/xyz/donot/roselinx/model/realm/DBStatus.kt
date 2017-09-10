package xyz.donot.roselinx.model.realm


import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*


open class DBStatus : RealmObject() {
    @PrimaryKey open var id: Long = 0L
    open var myid: Long = 0L
    open var type: Int = 0
    open var status: ByteArray? = null
    open var date: Date? = null
}
