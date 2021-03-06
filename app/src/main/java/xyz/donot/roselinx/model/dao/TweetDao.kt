package xyz.donot.roselinx.model.dao

import android.arch.paging.LivePagedListProvider
import android.arch.persistence.room.*
import xyz.donot.roselinx.model.entity.Tweet
import xyz.donot.roselinx.model.entity.TweetType
import xyz.donot.roselinx.model.entity.TweetUser

const val JOIN_TWEET = "tweet JOIN tweet_type JOIN tweet_user ON tweet.tweetId=tweet_type.tweetId AND tweet.tweetId=tweet_user.tweetId"
const val EQUALS_TYPE = "type=:type"
const val EQUALS_ME = "userId=:userId"
const val EQUALS_TWEETER = "tweeterId=:tweetedUserId"
const val SELECT_TWEET="status,date,tweet.tweetId,tweeterId"

@Dao
interface TweetDao {
    @Query("SELECT DISTINCT $SELECT_TWEET FROM $JOIN_TWEET WHERE $EQUALS_TYPE AND $EQUALS_ME order by date DESC")
    fun getAllDataSource(type: Int, userId: Long): LivePagedListProvider<Int,Tweet>

    @Query("SELECT DISTINCT $SELECT_TWEET FROM $JOIN_TWEET WHERE date=(SELECT MIN(date) FROM $JOIN_TWEET WHERE $EQUALS_ME AND $EQUALS_TYPE)")
    fun getOldestTweet(type: Int, userId: Long): Tweet

    @Query("SELECT DISTINCT $SELECT_TWEET FROM $JOIN_TWEET WHERE date=(SELECT MAX(date) FROM $JOIN_TWEET WHERE $EQUALS_ME AND $EQUALS_TYPE)")
    fun getNewestTweet(type: Int, userId: Long): Tweet

    //User
    @Query("SELECT DISTINCT $SELECT_TWEET FROM $JOIN_TWEET WHERE $EQUALS_TYPE AND $EQUALS_ME AND $EQUALS_TWEETER order by date DESC")
    fun getAllUserDataSource(type: Int, userId: Long,tweetedUserId:Long): LivePagedListProvider<Int,Tweet>

    @Query("SELECT DISTINCT $SELECT_TWEET FROM $JOIN_TWEET WHERE date=(SELECT MIN(date) FROM $JOIN_TWEET WHERE $EQUALS_ME AND $EQUALS_TYPE AND $EQUALS_TWEETER)")
    fun getUserOldestTweet(type: Int, userId: Long,tweetedUserId:Long): Tweet

    @Query("SELECT DISTINCT $SELECT_TWEET FROM $JOIN_TWEET WHERE date=(SELECT MAX(date) FROM $JOIN_TWEET WHERE $EQUALS_ME AND $EQUALS_TYPE AND $EQUALS_TWEETER)")
    fun getUserNewestTweet(type: Int, userId: Long,tweetedUserId:Long): Tweet

  //  @Insert(onConflict = OnConflictStrategy.REPLACE)
 //   fun insert(tweet: Tweet) ("INSERT INTO tweet(status,date,tweeterId,tweetId) VALUES (:tweet.status,:tweet.date,:tweet.tweeterId,:tweet.tweetId) ON DUPLICATE KEY UPDATE tweetId=:tweet.tweetId")

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

    @Update
    fun update(tweet: List<Tweet>)

    @Query("DELETE FROM tweet WHERE tweetId=:id")
    fun deleteById(id:Long)

    @Query("DELETE FROM tweet_type WHERE tweetId=:id")
    fun deleteTypeById(id:Long)

    @Query("DELETE FROM tweet_user WHERE tweetId=:id")
    fun deleteUserById(id:Long)
}

