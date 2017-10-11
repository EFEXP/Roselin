package xyz.donot.roselinx.model.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import xyz.donot.roselinx.model.entity.CustomProfile

@Dao
interface CustomProfileDao {

    @Query("SELECT * FROM custom_profile")
    fun getAllLiveData(): LiveData<List<CustomProfile>>

    @Query("SELECT * FROM custom_profile")
    fun getAllData(): List<CustomProfile>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCustomProfile(profile: CustomProfile): Long

}
