package xyz.donot.roselinx.model.realm


import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import twitter4j.Status
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.util.getSerialized
import java.util.*


open class HomeStatus : RealmObject() {
    @PrimaryKey open var id: Long = 0L
    open var myid: Long = 0L
    open var type: Int = 0
    open var status: ByteArray? = null
    open var date: Date? = null
}
fun addHomeStatus(statuses:List<Status>) {
    val myId= getMyId()
    Realm.getDefaultInstance().use {
        realm->
        realm.executeTransaction {
           for (item in statuses)
           {
               realm.createObject(HomeStatus::class.java).apply {
                   status=item.getSerialized()
                   id=item.id
                   myid=myId
                   date= item.createdAt
               }
           }
        }
    }
}