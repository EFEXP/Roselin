package xyz.donot.roselinx.model.dao

import android.arch.paging.LivePagedListProvider
import android.arch.persistence.room.*
import xyz.donot.roselinx.model.entity.Tweet
import xyz.donot.roselinx.model.entity.TweetType
import xyz.donot.roselinx.model.entity.TweetUser

const val JoinTweet = "tweet JOIN tweet_type JOIN tweet_user ON tweet.tweetId=tweet_type.tweetId AND tweet.tweetId=tweet_user.tweetId"
const val EqualsType = "type=:type"
const val EqualsUser = "userId=:userId"
const val EqualsUserTweet = "tweetedUserId=:tweetedUserId"
const val SELECT_TWEET="status,date,tweet.tweetId,tweetedUserId"

@Dao
interface TweetDao {
    @Query("SELECT DISTINCT $SELECT_TWEET FROM $JoinTweet WHERE $EqualsType AND $EqualsUser order by date DESC")
    fun getAllDataSource(type: Int, userId: Long): LivePagedListProvider<Int,Tweet>

    @Query("SELECT DISTINCT $SELECT_TWEET FROM $JoinTweet WHERE date=(SELECT MIN(date) FROM $JoinTweet WHERE $EqualsUser AND $EqualsType)")
    fun getOldestTweet(type: Int, userId: Long): Tweet

    @Query("SELECT DISTINCT $SELECT_TWEET FROM $JoinTweet WHERE date=(SELECT MAX(date) FROM $JoinTweet WHERE $EqualsUser AND $EqualsType)")
    fun getNewestTweet(type: Int, userId: Long): Tweet

    //User
    @Query("SELECT DISTINCT $SELECT_TWEET FROM $JoinTweet WHERE $EqualsType AND $EqualsUser AND $EqualsUserTweet order by date DESC")
    fun getAllUserDataSource(type: Int, userId: Long,tweetedUserId:Long): LivePagedListProvider<Int,Tweet>

    @Query("SELECT DISTINCT $SELECT_TWEET FROM $JoinTweet WHERE date=(SELECT MIN(date) FROM $JoinTweet WHERE $EqualsUser AND $EqualsType AND $EqualsUserTweet)")
    fun getUserOldestTweet(type: Int, userId: Long,tweetedUserId:Long): Tweet

    @Query("SELECT DISTINCT $SELECT_TWEET FROM $JoinTweet WHERE date=(SELECT MAX(date) FROM $JoinTweet WHERE $EqualsUser AND $EqualsType AND $EqualsUserTweet)")
    fun getUserNewestTweet(type: Int, userId: Long,tweetedUserId:Long): Tweet

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tweet: Tweet)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tweet:List<Tweet>)

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insertType(tweetType:List<TweetType>)

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insertMyUserId(tweetUser:List<TweetUser>)

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insertType(tweetType:TweetType)

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insertMyUserId(tweetUser:TweetUser)

    @Update
    fun update(tweet: Tweet)

    @Query("DELETE FROM tweet WHERE tweetId=:id")
    fun deleteById(id:Long)

/*
    @Query("SELECT * FROM tweet WHERE type=:type order by date DESC")
    fun getAllLiveData(type: Int): LiveData<List<Tweet>>

    @Query("SELECT * FROM tweet WHERE type=:type order by date DESC")
    fun getAllDataSource(type: Int): LivePagedListProvider<Int, Tweet>

    @Query("SELECT * FROM tweet WHERE type=:type AND userId=:userId order by date DESC")
    fun getAllUserDataSource(type: Int,userId: Long): LivePagedListProvider<Int, Tweet>

    @Query("SELECT COUNT(*) FROM tweet WHERE type=:type")
    fun countTweet(type:Int):Int

    @Query("SELECT * FROM tweet WHERE tweetId=:id")
    fun equalToIds(id:Long): LiveData<List<Tweet>>

    @Update
    fun update(tweet: Tweet)

    @Query("DELETE FROM tweet WHERE tweetId=:id")
    fun deleteById(id:Long)*/
}

