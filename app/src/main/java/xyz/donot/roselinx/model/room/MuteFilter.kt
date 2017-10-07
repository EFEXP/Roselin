package xyz.donot.roselinx.model.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.content.Context
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.User
import xyz.donot.roselinx.customrecycler.Diffable




@Entity(tableName = "mute_filter")
data class MuteFilter(
        var accountId: Long =0L,
        val text: String="" ,
        val kichitsui:Int=0,
        val user: User?
) : Diffable {
    @PrimaryKey(autoGenerate = true) var id: Long=0

    override fun isTheSame(other: Diffable) = id == (other as? MuteFilter)?.id

    companion object {
        fun save(context: Context,filter: MuteFilter) = launch (UI){
            async {RoselinDatabase.getInstance(context).muteFilterDao().insertMute(filter)}.await()
        }
    }
}
@Dao
interface MuteFilterDao {
    @Query("SELECT * FROM mute_filter")
    fun getAllData(): List<MuteFilter>

    @Query("SELECT * FROM mute_filter WHERE accountId!=0")
    fun getMuteUser(): LiveData<List<MuteFilter>>

    @Query("SELECT * FROM mute_filter WHERE accountId=0")
    fun getMuteWord(): LiveData<List<MuteFilter>>


    @Query("SELECT * FROM mute_filter")
    fun getAllLiveData(): LiveData<List<MuteFilter>>

    @Query("SELECT * FROM mute_filter WHERE accountId=:id")
    fun equalToAccountIdMute(id:Long): List<MuteFilter>

    @Query("SELECT * FROM mute_filter WHERE kichitsui=1")
    fun kichitsuiMuted(): List<MuteFilter>


    @Update
    fun update(filter: MuteFilter)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMute(filter: MuteFilter): Long

    @Delete
    fun delete(filter:MuteFilter)
}
