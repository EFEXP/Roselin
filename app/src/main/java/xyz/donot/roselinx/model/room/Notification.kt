package xyz.donot.roselinx.model.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Status
import twitter4j.User
import xyz.donot.roselinx.customrecycler.Diffable
import java.util.*


const val NFAVORITE=100
const val NRETWEET=200
@Entity(tableName = "notification")
data class Notification(
        val sourceUser: User,
        val status: Status,
        val type: Int = 0,
        val date: Date
) : Diffable {
    @PrimaryKey(autoGenerate = true) var id: Long = 0

    override fun isTheSame(other: Diffable) = id == (other as? Notification)?.id

    companion object {
        fun save(notice: Notification) = launch(UI) {
            async { RoselinDatabase.getInstance().notificationDao().insertNotification(notice) }.await()
        }
    }
}

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notification ORDER BY date DESC")
    fun getAllLiveData(): LiveData<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notice: Notification): Long

}

