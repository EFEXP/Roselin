package xyz.donot.roselinx.model.dao

import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListProvider
import android.arch.persistence.room.*
import xyz.donot.roselinx.model.entity.TweetDraft

@Dao
interface TweetDraftDao {
    @Query("SELECT * FROM tweet_draft")
    fun getAll(): List<TweetDraft>

    @Query("SELECT * FROM tweet_draft")
    fun getAllLiveData(): LivePagedListProvider<Int, TweetDraft>

    @Query("SELECT * FROM tweet_draft WHERE accountId=:id")
    fun equalToIdDrafts(id:Long): LiveData<List<TweetDraft>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDraft(draft: TweetDraft): Long

    @Delete
    fun delete(draft: TweetDraft)
}
