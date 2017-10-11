package xyz.donot.roselinx.model.dao

import android.arch.paging.LivePagedListProvider
import android.arch.persistence.room.*
import xyz.donot.roselinx.model.entity.TwitterAccount

@Dao
interface TwitterAccountDao {
    @Query("SELECT COUNT(*) FROM twitter_account")
    fun count(): Int

    @Query("SELECT * FROM twitter_account")
    fun allData(): LivePagedListProvider<Int, TwitterAccount>

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
