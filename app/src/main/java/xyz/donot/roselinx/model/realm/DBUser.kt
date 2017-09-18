package xyz.donot.roselinx.model.realm

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import twitter4j.User
import xyz.donot.roselinx.util.getSerialized

open class DBUser : RealmObject() {
    @PrimaryKey open var id: Long = 0L
    open var screenname: String = ""
    @Required
    open var user: ByteArray = ByteArray(0)

}
fun saveUser(user_: User) {
    Realm.getDefaultInstance().use {
        it.executeTransaction {
            it.insertOrUpdate(
                    DBUser().apply {
                        screenname = user_.screenName
                        id = user_.id
                        user = user_.getSerialized()
                    }
            )
        }
        }
}
