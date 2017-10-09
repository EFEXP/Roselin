package xyz.donot.roselinx.model.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Status
import xyz.donot.roselinx.customrecycler.Diffable
import java.util.*

const val HOME_TIMELINE:Int=1000
const val MENTION_TIMELINE:Int=2000
const val USER_TIMELINE:Int=3000
@Entity(tableName = "tweet")
data class Tweet(
        val status: Status,
        val userId:Long=0L,
        val type:Int,
        val date: Date,
        @PrimaryKey(autoGenerate =false) val tweetId: Long
) : Diffable {

    override fun isTheSame(other: Diffable) = tweetId == (other as? Tweet)?.tweetId

    companion object {
        fun save(status: Status,type:Int,userId: Long=0L) = launch (UI){
            async {RoselinDatabase.getInstance().tweetDao().insert(Tweet(status,userId,type,status.createdAt,status.id)) }.await()
        }
        fun save(status:List<Status>,type:Int,userId: Long=0L) = launch (UI){
            async {RoselinDatabase.getInstance().tweetDao().insert(status.map { Tweet(it,userId,type,it.createdAt,it.id) }.toTypedArray()) }.await()
        }
    }
}

@Dao
interface TweetDao {
    @Query("SELECT * FROM tweet WHERE type=:type order by date DESC")
    fun getAllLiveData(type: Int): LiveData<List<Tweet>>

    @Query("SELECT * FROM tweet WHERE date=(SELECT MIN(date) FROM tweet WHERE type=:type)")
    fun getOldestTweet(type:Int):Tweet

    @Query("SELECT * FROM tweet WHERE date=(SELECT MAX(date) FROM tweet WHERE type=:type)")
    fun getNewestTweet(type:Int):Tweet

    @Query("SELECT COUNT(*) FROM tweet WHERE type=:type")
    fun countTweet(type:Int):Int

    @Query("SELECT * FROM tweet WHERE tweetId=:id")
    fun equalToIds(id:Long): LiveData<List<Tweet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tweet: Tweet): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tweet:Array<Tweet>)

    @Query("DELETE FROM tweet WHERE tweetId=:id")
    fun deleteById(id:Long)
}