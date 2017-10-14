package xyz.donot.roselinx.model.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Status
import twitter4j.User
import xyz.donot.roselinx.ui.util.diff.Distinguishable
import java.util.*


const val NFAVORITE=100
const val NRETWEET=200
@Entity(tableName = "notification")
data class Notification(
        val sourceUser: User,
        val status: Status,
        val type: Int = 0,
        val date: Date
) : Distinguishable {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
    override fun isTheSame(other: Distinguishable) = id == (other as? Notification)?.id
    companion object {
        fun save(notice: Notification) = launch(UI) {
            async { RoselinDatabase.getInstance().notificationDao().insertNotification(notice) }.await()
        }
    }
}



