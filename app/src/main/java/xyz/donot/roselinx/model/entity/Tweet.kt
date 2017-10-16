package xyz.donot.roselinx.model.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
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
                instance.tweetDao().insertType(TweetType(type, status.id))
            }
        }

        fun save(status: List<Status>, type: Int, userId: Long) = launch {
            val instance = RoselinDatabase.getInstance()
            instance.runInTransaction {
                instance.tweetDao().insert(status.map { Tweet(it, it.createdAt, it.user.id, it.id) })
                instance.tweetDao().insertMyUserId(status.map { TweetUser(userId, it.id) })
                instance.tweetDao().insertType(status.map { TweetType(type, it.id) })
            }
        }

        fun update(status: Status) = launch {
            val instance = RoselinDatabase.getInstance().tweetDao()
            instance.update(Tweet(status, status.createdAt, status.user.id, status.id))
        }

        fun delete(id: Long) = launch {
            val instance = RoselinDatabase.getInstance()
            instance.runInTransaction {
                instance.compileStatement("DELETE FROM tweet WHERE tweetId=$id").executeUpdateDelete()
                instance.compileStatement("DELETE FROM tweet_type WHERE tweetId=$id").executeUpdateDelete()
                instance.compileStatement("DELETE FROM tweet_user WHERE tweetId=$id").executeUpdateDelete()
               // instance.tweetDao().deleteUserById(id)
              //  instance.tweetDao().deleteTypeById(id)
               // instance.tweetDao().deleteById(id)
            }

        }
    }
}

const val HOME_TIMELINE: Int = 1000
const val MENTION_TIMELINE: Int = 2000
const val USER_TIMELINE: Int = 3000

@Entity(tableName = "tweet_type",
        foreignKeys = arrayOf(ForeignKey(entity = Tweet::class,
                parentColumns = arrayOf("tweetId"),
                childColumns = arrayOf("tweetId"),
                onUpdate = ForeignKey.NO_ACTION,
                onDelete = ForeignKey.NO_ACTION)))
data class TweetType(val type: Int,
                     val tweetId: Long) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

//自分のIDを入れること
@Entity(tableName = "tweet_user", foreignKeys = arrayOf(ForeignKey(entity = Tweet::class,
        parentColumns = arrayOf("tweetId"),
        childColumns = arrayOf("tweetId"),
        onUpdate = ForeignKey.NO_ACTION,
        onDelete = ForeignKey.NO_ACTION)))
data class TweetUser(val userId: Long,
                     val tweetId: Long) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}