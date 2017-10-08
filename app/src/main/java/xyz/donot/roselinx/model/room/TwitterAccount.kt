package xyz.donot.roselinx.model.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.content.Context
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Twitter
import twitter4j.User
import xyz.donot.roselinx.customrecycler.Diffable

@Entity(tableName = "twitter_account")
data class TwitterAccount(
        var isMain:Boolean,
        val user:User,
        val account: Twitter,
        @PrimaryKey(autoGenerate = false) val id: Long
): Diffable {
    override fun isTheSame(other: Diffable) = id == (other as? SavedTab)?.id
    companion object {
        fun save(context: Context, account: TwitterAccount) = launch(UI) {
            async { RoselinDatabase.getInstance().twitterAccountDao().insertUser(account) }.await()
        }
    }
}

@Dao
interface TwitterAccountDao {
    @Query("SELECT COUNT(*) FROM twitter_account")
    fun count(): Int

    @Query("SELECT * FROM twitter_account")
    fun allData(): LiveData<List<TwitterAccount>>

    @Query("SELECT * FROM twitter_account WHERE id =:userId LIMIT 1")
    fun findById(userId: Long): TwitterAccount

    @Update
    fun update(account: TwitterAccount)

    @Query("SELECT * FROM twitter_account WHERE isMain=:isMain LIMIT 1")
    fun getMainAccount(isMain:Boolean): TwitterAccount

    @Query("DELETE FROM twitter_account")
    fun deleteAll()

    @Query("DELETE FROM twitter_account WHERE id =:userId")
    fun deleteById(userId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(account: TwitterAccount)
}

