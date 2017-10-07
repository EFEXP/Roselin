package xyz.donot.roselinx.model.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.content.Context
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.customrecycler.Diffable

@Entity(tableName = "tweet_draft")
data class TweetDraft(
        var accountId: Long ,
        val text: String ,
        val replyToStatusId: Long = 0,
        val replyToScreenName: String = ""
) : Diffable {
    @PrimaryKey(autoGenerate = true) var id: Long=0

    override fun isTheSame(other: Diffable) = id == (other as? TweetDraft)?.id

    companion object {
        fun save(context: Context, tweetDraft: TweetDraft) = launch (UI){
            async {RoselinDatabase.getInstance(context).tweetDraftDao().insertDraft(tweetDraft) }.await()
        }
    }
}

@Dao
interface TweetDraftDao {
    @Query("SELECT * FROM tweet_draft")
    fun getAll(): List<TweetDraft>

    @Query("SELECT * FROM tweet_draft")
    fun getAllLiveData(): LiveData<List<TweetDraft>>

    @Query("SELECT * FROM tweet_draft WHERE accountId=:id")
    fun equalToIdDrafts(id:Long): LiveData<List<TweetDraft>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDraft(draft: TweetDraft): Long

    @Delete
    fun delete(draft: TweetDraft)
}