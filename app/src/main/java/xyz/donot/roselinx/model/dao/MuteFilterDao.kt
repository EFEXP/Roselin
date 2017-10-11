package xyz.donot.roselinx.model.dao

import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListProvider
import android.arch.persistence.room.*
import xyz.donot.roselinx.model.entity.MuteFilter

@Dao
interface MuteFilterDao {
    @Query("SELECT * FROM mute_filter")
    fun getAllData(): List<MuteFilter>

    @Query("SELECT * FROM mute_filter WHERE accountId!=0")
    fun getMuteUser(): LivePagedListProvider<Int, MuteFilter>

    @Query("SELECT * FROM mute_filter WHERE accountId=0")
    fun getMuteWord(): LivePagedListProvider<Int, MuteFilter>


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
    fun delete(filter: MuteFilter)
}
