package xyz.donot.roselinx.model.entity

import android.arch.persistence.room.*
import android.content.Context
import twitter4j.Query
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.User
import xyz.donot.roselinx.ContextHolder
import xyz.donot.roselinx.model.dao.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*


@Database(entities = arrayOf(TwitterAccount::class, UserData::class, TweetDraft::class, MuteFilter::class, Notification::class, SavedTab::class, CustomProfile::class, Tweet::class, TweetType::class, TweetUser::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RoselinDatabase : RoomDatabase() {
    abstract fun twitterAccountDao(): TwitterAccountDao
    abstract fun userDataDao(): UserDataDao
    abstract fun tweetDraftDao(): TweetDraftDao
    abstract fun muteFilterDao(): MuteFilterDao
    abstract fun notificationDao(): NotificationDao
    abstract fun savedTabDao(): SavedTabDao
    abstract fun customProfileDao(): CustomProfileDao
    abstract fun tweetDao(): TweetDao
    companion object {
        @Volatile private var INSTANCE: RoselinDatabase? = null
        @Volatile private var MAIN_THREAD_INSTANCE: RoselinDatabase? = null
        fun getInstance(): RoselinDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(ContextHolder.getContext(),false).also { INSTANCE = it }
                }
        fun getAllowedInstance(): RoselinDatabase =
                MAIN_THREAD_INSTANCE ?: synchronized(this) {
                    MAIN_THREAD_INSTANCE ?: buildDatabase(ContextHolder.getContext(),true).also {   MAIN_THREAD_INSTANCE = it }
                }

        private fun buildDatabase(context: Context, canExecuteOnMainThread: Boolean):RoselinDatabase {
            val instance = Room.databaseBuilder(context.applicationContext, RoselinDatabase::class.java, "roselin.db")
            if (canExecuteOnMainThread)
                instance.allowMainThreadQueries()
            return instance.build()

        }

    }

}

class Converters {
    companion object {
        //Date
        @TypeConverter
        @JvmStatic
        fun fromTimeToDate(time: Long): Date? {
            return if (time == 0L) null else Date(time)
        }

        @TypeConverter
        @JvmStatic
        fun fromDateToTime(date: Date?): Long {
            return date?.time ?: 0L
        }

        //Query
        @TypeConverter
        @JvmStatic
        fun nullableQuerySerialize(value: Query?): ByteArray? {
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
        @JvmStatic
        fun nullableQueryDeserialize(byteArray: ByteArray?): Query? {
            if (byteArray != null)
                ByteArrayInputStream(byteArray).use { stream ->
                    ObjectInputStream(stream).use {
                        return it.readObject() as Query
                    }
                }
            else
                return null
        }

        //user

        @TypeConverter
        @JvmStatic
        fun nullableUserDeserialize(byteArray: ByteArray?): User? {
            if (byteArray != null)
                ByteArrayInputStream(byteArray).use { stream ->
                    ObjectInputStream(stream).use {
                        return it.readObject() as User
                    }
                }
            else
                return null
        }

        @TypeConverter
        @JvmStatic
        fun nullableUserSerialize(value: User?): ByteArray? {
            if (value != null)
                ByteArrayOutputStream().use {
                    ObjectOutputStream(it).use {
                        it.writeObject(value)
                    }
                    return it.toByteArray()
                }
            else return null
        }

        //Twitter
        @TypeConverter
        @JvmStatic
        fun deserialize(byteArray: ByteArray): Twitter {
            ByteArrayInputStream(byteArray).use { stream ->
                ObjectInputStream(stream).use {
                    return it.readObject() as Twitter
                }
            }
        }

        @TypeConverter
        @JvmStatic
        fun serialize(value: Twitter): ByteArray {
            ByteArrayOutputStream().use {
                ObjectOutputStream(it).use {
                    it.writeObject(value)
                }
                return it.toByteArray()
            }
        }

        //Boolean
        @TypeConverter
        @JvmStatic
        fun booleanToInt(value: Boolean): Int {
            return if (value) 1 else 0
        }

        @TypeConverter
        @JvmStatic
        fun intToBoolean(value: Int): Boolean {
            return value != 0

        }


        //Status
        @TypeConverter
        @JvmStatic
        fun statusSerialize(value: Status): ByteArray {
            ByteArrayOutputStream().use {
                ObjectOutputStream(it).use {
                    it.writeObject(value)
                }
                return it.toByteArray()
            }
        }

        @TypeConverter
        @JvmStatic
        fun statusDeserialize(byteArray: ByteArray): Status {
            ByteArrayInputStream(byteArray).use { stream ->
                ObjectInputStream(stream).use {
                    return it.readObject() as Status
                }
            }
        }


    }
}

