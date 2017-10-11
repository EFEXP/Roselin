package xyz.donot.roselinx.model.dao

import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListProvider
import android.arch.persistence.room.*
import xyz.donot.roselinx.model.entity.Tweet

@Dao
interface TweetDao {
    @Query("SELECT * FROM tweet WHERE type=:type order by date DESC")
    fun getAllLiveData(type: Int): LiveData<List<Tweet>>

    @Query("SELECT * FROM tweet WHERE type=:type order by date DESC")
    fun getAllDataSource(type: Int): LivePagedListProvider<Int, Tweet>

    @Query("SELECT * FROM tweet WHERE date=(SELECT MIN(date) FROM tweet WHERE type=:type)")
    fun getOldestTweet(type:Int): Tweet

    @Query("SELECT * FROM tweet WHERE date=(SELECT MAX(date) FROM tweet WHERE type=:type)")
    fun getNewestTweet(type:Int): Tweet

    @Query("SELECT COUNT(*) FROM tweet WHERE type=:type")
    fun countTweet(type:Int):Int

    @Query("SELECT * FROM tweet WHERE tweetId=:id")
    fun equalToIds(id:Long): LiveData<List<Tweet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tweet: Tweet): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tweet:List<Tweet>)

    @Update
    fun update(tweet: Tweet)

    @Query("DELETE FROM tweet WHERE tweetId=:id")
    fun deleteById(id:Long)
}
