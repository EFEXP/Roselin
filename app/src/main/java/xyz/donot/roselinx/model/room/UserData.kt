package xyz.donot.roselinx.model.room

import android.arch.persistence.room.*
import kotlinx.coroutines.experimental.launch
import twitter4j.User
import xyz.donot.roselinx.customrecycler.Diffable


@Entity(tableName = "user_data")
data class UserData(
        val user: User,
        val screenname: String,
        @PrimaryKey(autoGenerate = false) val id: Long
): Diffable {
    override fun isTheSame(other: Diffable) = id == (other as? SavedTab)?.id
    companion object {
  fun save(user_:UserData)=  launch {
        RoselinDatabase.getInstance().userDataDao().insertUser(user_)
    }
        fun saveAll(user_:Array<UserData>)=  launch {
            RoselinDatabase.getInstance().userDataDao().insertUsers(user_)
        }
    }
}

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
    fun insertUser(user:UserData):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(user:Array<UserData>)

    @Query("DELETE FROM user_data")
    fun deleteAll()

    @Query("DELETE FROM user_data WHERE id =:userId")
    fun deleteById(userId: Long)
}


