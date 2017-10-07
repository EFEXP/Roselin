package xyz.donot.roselinx.model

import android.arch.persistence.room.*
import android.content.Context
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.User
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

@Database(entities = arrayOf(TwitterAccount::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RoselinDatabase : RoomDatabase() {
    abstract fun userRoomDao(): UserRoomDao
    companion object {

        @Volatile private var INSTANCE:  RoselinDatabase? = null

        fun getInstance(context: Context): RoselinDatabase=
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        RoselinDatabase::class.java, "roselin.db")
                        .build()
    }

}

class Converters {
    companion object {
        @TypeConverter
        @JvmStatic fun userSerialize(value: User): ByteArray {
            ByteArrayOutputStream().use {
                ObjectOutputStream(it).use {
                    it.writeObject(value)
                }
                return it.toByteArray()
            }
        }




        @TypeConverter
        @JvmStatic fun serialize(value: Twitter): ByteArray {
            ByteArrayOutputStream().use {
                ObjectOutputStream(it).use {
                    it.writeObject(value)
                }
                return it.toByteArray()
            }
        }


        @TypeConverter
        @JvmStatic fun statusSerialize(value: Status): ByteArray {
            ByteArrayOutputStream().use {
                ObjectOutputStream(it).use {
                    it.writeObject(value)
                }
                return it.toByteArray()
            }
        }

        @TypeConverter
        @JvmStatic fun statusDeserialize(byteArray: ByteArray): Status {
            @Suppress("UNCHECKED_CAST")
            ByteArrayInputStream(byteArray).use { stream ->
                ObjectInputStream(stream).use {
                    return it.readObject() as Status
                }
            }
        }

        @TypeConverter
        @JvmStatic fun deserialize(byteArray: ByteArray): Twitter {
            @Suppress("UNCHECKED_CAST")
            ByteArrayInputStream(byteArray).use { stream ->
                ObjectInputStream(stream).use {
                    return it.readObject() as Twitter
                }
            }
        }
        @TypeConverter
        @JvmStatic fun userDeserialize(byteArray: ByteArray): User {
            @Suppress("UNCHECKED_CAST")
            ByteArrayInputStream(byteArray).use { stream ->
                ObjectInputStream(stream).use {
                    return it.readObject() as User
                }
            }
        }
    }}