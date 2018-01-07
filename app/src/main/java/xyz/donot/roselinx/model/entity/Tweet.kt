package xyz.donot.roselinx.model.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import kotlinx.coroutines.experimental.launch
import twitter4j.Status
import xyz.donot.roselinx.ui.util.diff.Distinguishable
import java.util.*

@Entity(tableName = "tweet")
data class Tweet(
        val status: Status,
        val date: Date,
        val tweeterId: Long,
        @PrimaryKey(autoGenerate = false) val tweetId: Long
) : Distinguishable {
    override fun isTheSame(other: Distinguishable) = tweetId == (other as? Tweet)?.tweetId
    companion object {
        fun save(status: Status, type: Int, userId: Long) = launch {
            val instance = RoselinDatabase.getInstance()
            instance.runInTransaction {
                instance.tweetDao().insert(Tweet(status, status.createdAt, status.user.id, status.id))
                instance.tweetDao().insertMyUserId(TweetUser(userId, status.id))
                instance.tweetDao().insertTypeToTweet(TypeToTweet(type,status.id))
            }
        }

        fun save(status: List<Status>, type: Int, userId: Long) = launch {
            val instance = RoselinDatabase.getInstance()
            instance.runInTransaction {
                instance.tweetDao().insert(status.map { Tweet(it, it.createdAt, it.user.id, it.id) })
                instance.tweetDao().insertMyUserId(status.map { TweetUser(userId, it.id) })
                instance.tweetDao().insertTypeToTweet(status.map { TypeToTweet(type,it.id)})
            }
        }

        fun update(status: Status) = launch {
            val instance = RoselinDatabase.getInstance().tweetDao()
            instance.update(Tweet(status, status.createdAt, status.user.id, status.id))
        }

        fun delete(id: Long,type:Int) = launch {
            val instance = RoselinDatabase.getInstance()
            instance.runInTransaction {
                instance.compileStatement("DELETE FROM tweet WHERE tweetId=$id").executeUpdateDelete()
                instance.compileStatement("DELETE FROM type_to_tweet WHERE tweetId=$id AND type=$type").executeUpdateDelete()
                instance.compileStatement("DELETE FROM tweet_user WHERE tweetId=$id").executeUpdateDelete()
            }
        }

        fun initType() = launch {
            val instance = RoselinDatabase.getInstance()
            instance.tweetDao().insertType(arrayListOf(TweetType(HOME_TIMELINE), TweetType(MENTION_TIMELINE), TweetType(USER_TIMELINE)))
        }
    }
}

const val HOME_TIMELINE: Int = 1000
const val MENTION_TIMELINE: Int = 2000
const val USER_TIMELINE: Int = 3000

//1000 親カラム
@Entity(tableName = "tweet_type")
data class TweetType(@PrimaryKey val typeId:Int)

//1000|462813636289
@Entity(tableName = "type_to_tweet",primaryKeys = ["typeId", "tweetId"] )
data class TypeToTweet(val typeId:Int, val tweetId: Long)

//自分の選択アカウントのタイムラインを識別 43462746282|423678235628
@Entity(tableName = "tweet_user",primaryKeys = ["userId", "tweetId"] )
data class TweetUser(val userId: Long, val tweetId: Long)