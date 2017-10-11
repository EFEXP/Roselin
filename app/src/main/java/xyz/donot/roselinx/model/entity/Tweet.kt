package xyz.donot.roselinx.model.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
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
            async {RoselinDatabase.getInstance().tweetDao().insert(status.map { Tweet(it,userId,type,it.createdAt,it.id) }) }.await()
        }
        fun update(status:Status,type: Int) = launch (UI){
            async {RoselinDatabase.getInstance().tweetDao().update(Tweet(status,status.user.id,type,status.createdAt,status.id) ) }.await()
        }
    }
}

