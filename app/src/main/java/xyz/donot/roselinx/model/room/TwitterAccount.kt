package xyz.donot.roselinx.model.room

import android.arch.persistence.room.*
import twitter4j.Twitter
import twitter4j.User

@Entity(tableName = "twitter_account")
data class TwitterAccount(
        var isMain: Boolean,
        val user:User,
        val account: Twitter,
        @PrimaryKey(autoGenerate = false) val id: Long
)

@Dao
interface TwitterAccountDao {
    @Query("SELECT * FROM twitter_account WHERE id =:userId LIMIT 1")
    fun findById(userId: Long): TwitterAccount

    @Query("SELECT * FROM twitter_account LIMIT 1")
    fun getMyAccount(): TwitterAccount

    @Query("DELETE FROM twitter_account")
    fun deleteAll()

    @Query("DELETE FROM twitter_account WHERE id =:userId")
    fun deleteById(userId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(userEntity: TwitterAccount)
}

