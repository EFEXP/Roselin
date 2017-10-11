package xyz.donot.roselinx.model.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import xyz.donot.roselinx.model.entity.SavedTab


@Dao
interface SavedTabDao {

    @Query("SELECT COUNT(*) FROM saved_tab")
    fun countTab(): Int

    @Query("SELECT MAX(tabOrder) FROM saved_tab  ")
    fun maxOrder(): Int

    @Query("SELECT * FROM saved_tab ORDER BY tabOrder")
    fun getAllLiveData(): LiveData<List<SavedTab>>

    @Query("SELECT * FROM saved_tab ORDER BY tabOrder")
    fun getAllData(): List<SavedTab>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSavedTab(tab: SavedTab): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSavedTabs(tab: Array<SavedTab>)

    @Delete
    fun delete(tab: SavedTab)

    @Query("DELETE FROM saved_tab")
    fun deleteAll()
}

