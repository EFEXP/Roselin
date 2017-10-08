package xyz.donot.roselinx.model.realm

/*
const val HOME_TIMELINE:Int=1000
open  class StatusObject : RealmObject() {
    open  var status: ByteArray?=null
    open  var type: Int =0
    open var date: Date? =null
   @PrimaryKey open var id:Long =0L
}
fun saveStatus(status_:Status,type_:Int) {
    Realm.getDefaultInstance().use {
        it.executeTransaction {
            it.insertOrUpdate(
                    StatusObject().apply {
                        status=status_.getSerialized()
                        type=type_
                        date=status_.createdAt
                        id=status_.id
                    }
            )
        }
    }
}*/