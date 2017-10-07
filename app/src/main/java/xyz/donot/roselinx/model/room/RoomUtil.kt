package xyz.donot.roselinx.model.room

import android.arch.persistence.room.*
import android.content.Context
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.User
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

@Database(entities = arrayOf(TwitterAccount::class, UserData::class, TweetDraft::class, MuteFilter::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RoselinDatabase : RoomDatabase() {
    abstract fun twitterAccountDao(): TwitterAccountDao
    abstract fun userDataDao(): UserDataDao
    abstract fun tweetDraftDao(): TweetDraftDao
    abstract fun muteFilterDao(): MuteFilterDao

    companion object {
        @Volatile private var INSTANCE: RoselinDatabase? = null

        fun getInstance(context: Context): RoselinDatabase =
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
        @JvmStatic fun nullableUserSerialize(value: User?): ByteArray? {
            if (value != null)
                ByteArrayOutputStream().use {
                    ObjectOutputStream(it).use {
                        it.writeObject(value)
                    }
                    return it.toByteArray()
                }
            else return null
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
        @JvmStatic fun nullableUserDeserialize(byteArray: ByteArray?): User? {
            if (byteArray != null)
                @Suppress("UNCHECKED_CAST")
                ByteArrayInputStream(byteArray).use { stream ->
                    ObjectInputStream(stream).use {
                        return it.readObject() as User
                    }
                }
            else
                return null
        }
    }
}

