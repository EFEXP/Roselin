package xyz.donot.roselinx.model.dao

import android.arch.paging.DataSource
import android.arch.persistence.room.*
import xyz.donot.roselinx.model.entity.Tweet
import xyz.donot.roselinx.model.entity.TweetType
import xyz.donot.roselinx.model.entity.TweetUser
import xyz.donot.roselinx.model.entity.TypeToTweet

const val JOIN_TWEET = "tweet " +
        "JOIN type_to_tweet " +
        "ON tweet.tweetId=type_to_tweet.tweetId " +
        "JOIN tweet_user " +
        "ON tweet.tweetId=tweet_user.tweetId "+
        "JOIN tweet_type " +
        "ON type_to_tweet.typeId=tweet_type.typeId"

const val EQUALS_TYPE = "type_to_tweet.typeId=:type"
const val EQUALS_ME = "userId=:userId"
const val EQUALS_TWEETER = "tweeterId=:tweetedUserId"
const val SELECT_TWEET="status,date,tweet.tweetId,tweeterId"

@Dao
interface TweetDao {
    @Query("SELECT DISTINCT $SELECT_TWEET FROM $JOIN_TWEET WHERE $EQUALS_TYPE AND $EQUALS_ME order by date DESC")
    fun getAllDataSource(type: Int, userId: Long): DataSource.Factory<Int,Tweet>

    @Query("SELECT DISTINCT $SELECT_TWEET FROM $JOIN_TWEET WHERE date=(SELECT MIN(date) FROM $JOIN_TWEET WHERE $EQUALS_ME AND $EQUALS_TYPE)")
    fun getOldestTweet(type: Int, userId: Long): Tweet

    @Query("SELECT DISTINCT $SELECT_TWEET FROM $JOIN_TWEET WHERE date=(SELECT MAX(date) FROM $JOIN_TWEET WHERE $EQUALS_ME AND $EQUALS_TYPE)")
    fun getNewestTweet(type: Int, userId: Long): Tweet

    //User
    @Query("SELECT DISTINCT $SELECT_TWEET FROM $JOIN_TWEET WHERE $EQUALS_TYPE AND $EQUALS_ME AND $EQUALS_TWEETER order by date DESC")
    fun getAllUserDataSource(type: Int, userId: Long,tweetedUserId:Long): DataSource.Factory<Int,Tweet>

    @Query("SELECT DISTINCT $SELECT_TWEET FROM $JOIN_TWEET WHERE date=(SELECT MIN(date) FROM $JOIN_TWEET WHERE $EQUALS_ME AND $EQUALS_TYPE AND $EQUALS_TWEETER)")
    fun getUserOldestTweet(type: Int, userId: Long,tweetedUserId:Long): Tweet

    @Query("SELECT DISTINCT $SELECT_TWEET FROM $JOIN_TWEET WHERE date=(SELECT MAX(date) FROM $JOIN_TWEET WHERE $EQUALS_ME AND $EQUALS_TYPE AND $EQUALS_TWEETER)")
    fun getUserNewestTweet(type: Int, userId: Long,tweetedUserId:Long): Tweet

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(tweet: Tweet)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tweet:List<Tweet>)

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insertType(tweetType:List<TweetType>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMyUserId(tweetUser:List<TweetUser>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTypeToTweet(typeToTweet:List<TypeToTweet>)

    @Insert
    fun insertMyUserId(tweetUser:TweetUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTypeToTweet(typeToTweet: TypeToTweet)

    @Update
    fun update(tweet: Tweet)

    @Update
    fun update(tweet: List<Tweet>)

    @Query("DELETE FROM tweet WHERE tweetId=:id")
    fun deleteById(id:Long)

    @Query("DELETE FROM tweet_user WHERE tweetId=:id")
    fun deleteUserById(id:Long)
}

