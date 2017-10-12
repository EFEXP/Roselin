package xyz.donot.roselinx.model.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import kotlinx.coroutines.experimental.launch
import twitter4j.User
import xyz.donot.roselinx.ui.util.diff.Distinguishable


@Entity(tableName = "user_data")
data class UserData(
        val user: User,
        val screenname: String,
        @PrimaryKey(autoGenerate = false) val id: Long
): Distinguishable {
    override fun isTheSame(other: Distinguishable) = id == (other as? SavedTab)?.id
    companion object {
  fun save(user_:UserData)=  launch {
        RoselinDatabase.getInstance().userDataDao().insertUser(user_)
    }
        fun saveAll(user_:Array<UserData>)=  launch {
            RoselinDatabase.getInstance().userDataDao().insertUsers(user_)
        }
    }
}



