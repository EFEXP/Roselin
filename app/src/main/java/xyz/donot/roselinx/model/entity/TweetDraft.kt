package xyz.donot.roselinx.model.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.ui.util.diff.Distinguishable

@Entity(tableName = "tweet_draft")
data class TweetDraft(
        var accountId: Long ,
        val text: String ,
        val replyToStatusId: Long = 0,
        val replyToScreenName: String = ""
) : Distinguishable {
    @PrimaryKey(autoGenerate = true) var id: Long=0
    override fun isTheSame(other: Distinguishable) = id == (other as? TweetDraft)?.id
    companion object {
        fun save(tweetDraft: TweetDraft) = launch (UI){
            async {RoselinDatabase.getInstance().tweetDraftDao().insertDraft(tweetDraft) }.await()
        }
    }
}

