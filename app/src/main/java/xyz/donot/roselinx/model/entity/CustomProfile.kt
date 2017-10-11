package xyz.donot.roselinx.model.entity

import android.arch.persistence.room.Entity
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.customrecycler.Diffable
import java.util.*


@Entity(tableName = "custom_profile")
data class CustomProfile(
        var customname: String?=null,
        @android.arch.persistence.room.PrimaryKey(autoGenerate = false) val userId:Long,
        var memo: String?=null,
        var birthday: Date?=null
) : Diffable {
    override fun isTheSame(other: Diffable) = userId == (other as? CustomProfile)?.userId
    companion object {
        fun save(profile: CustomProfile) = launch {
           RoselinDatabase.getInstance().customProfileDao().insertCustomProfile(profile)
        }
    }
}


