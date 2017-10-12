package xyz.donot.roselinx.model.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Twitter
import twitter4j.User
import xyz.donot.roselinx.ui.util.diff.Distinguishable

@Entity(tableName = "twitter_account")
data class TwitterAccount(
        var isMain:Boolean,
        val user:User,
        val account: Twitter,
        @PrimaryKey(autoGenerate = false) val id: Long
): Distinguishable {
    override fun isTheSame(other: Distinguishable) = id == (other as? SavedTab)?.id
    companion object {
        fun save(account: TwitterAccount) = launch(UI) {
            async { RoselinDatabase.getInstance().twitterAccountDao().insertUser(account) }.await()
        }
    }
}


