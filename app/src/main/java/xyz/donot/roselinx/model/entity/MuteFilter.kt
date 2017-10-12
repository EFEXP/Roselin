package xyz.donot.roselinx.model.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.User
import xyz.donot.roselinx.customrecycler.Diffable

@Entity(tableName = "mute_filter")
data class MuteFilter(
        var accountId: Long =0L,
        val text: String?=null ,
        val kichitsui:Int=0,
        val user: User?
) : Diffable {
    @PrimaryKey(autoGenerate = true) var id: Long=0

    override fun isTheSame(other: Diffable) = id == (other as? MuteFilter)?.id

    companion object {
        fun save(filter: MuteFilter) = launch (UI){
            async {RoselinDatabase.getInstance().muteFilterDao().insertMute(filter)}.await()
        }
    }
}

