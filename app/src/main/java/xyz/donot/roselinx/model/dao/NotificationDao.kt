package xyz.donot.roselinx.model.dao

import android.arch.paging.LivePagedListProvider
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import xyz.donot.roselinx.model.entity.Notification

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notification ORDER BY date DESC")
    fun getAllData(): LivePagedListProvider<Int, Notification>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notice: Notification): Long

}