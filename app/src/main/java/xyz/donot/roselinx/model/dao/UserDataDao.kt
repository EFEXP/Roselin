package xyz.donot.roselinx.model.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import xyz.donot.roselinx.model.entity.UserData

@Dao
interface UserDataDao {
    @Query("SELECT * FROM user_data  LIMIT 1")
    fun  hasUser(): UserData

    @Query("SELECT * FROM user_data WHERE screenname=:screenname  LIMIT 1")
    fun  findByScreenName(screenname: String): UserData

    @Query("SELECT * FROM user_data WHERE id=:userId  LIMIT 1")
    fun  findById(userId: Long): UserData

    @Query("SELECT * FROM user_data")
    fun getAll(): List<UserData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserData):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(user:Array<UserData>)

    @Query("DELETE FROM user_data")
    fun deleteAll()

    @Query("DELETE FROM user_data WHERE id =:userId")
    fun deleteById(userId: Long)
}
