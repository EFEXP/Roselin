package xyz.donot.roselinx.ui.main

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.support.v4.app.RemoteInput
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Status
import twitter4j.StatusUpdate
import twitter4j.User
import xyz.donot.roselinx.R
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.model.entity.HOME
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.model.entity.SETTING
import xyz.donot.roselinx.model.entity.SavedTab
import xyz.donot.roselinx.receiver.ConnectionReceiver
import xyz.donot.roselinx.receiver.MusicReceiver
import xyz.donot.roselinx.service.REPLY_ID
import xyz.donot.roselinx.service.SearchStreamService
import xyz.donot.roselinx.service.StreamingService
import xyz.donot.roselinx.ui.util.getAccount
import xyz.donot.roselinx.ui.view.SingleLiveEvent
import xyz.donot.roselinx.util.extraUtils.*


class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val receiver by lazy { MusicReceiver() }
    val connectionReceiver by lazy { ConnectionReceiver() }
    private val sendReplyReceiver by lazy { SendReplyReceiver() }
    private val twitter by lazy { getAccount() }
    val postSucceed = MutableLiveData<Status>()
    val deleteSucceed = SingleLiveEvent<Unit>()

    fun registerReceivers() {
        val app: Roselin = getApplication()
        LocalBroadcastManager.getInstance(app).apply {
            // registerReceiver(disConnectionReceiver, IntentFilter("OnDisconnect"))
            registerReceiver(connectionReceiver, connectionReceiver.intentFilter)
        }

        app.registerReceiver(receiver, receiver.intentFilter)
        app.registerReceiver(sendReplyReceiver, IntentFilter())
    }


    //realm
    fun initTab() {
        launch(UI) {
            val count = async { RoselinDatabase.getInstance().savedTabDao().countTab() }.await()
            if (count <= 0) {
                async {
                    val maxOrder = RoselinDatabase.getInstance().savedTabDao().maxOrder()
                    RoselinDatabase.getInstance().savedTabDao().insertSavedTabs(arrayOf(
                            SavedTab(type = SETTING, tabOrder = maxOrder + 1),
                            SavedTab(type = HOME, accountId = twitter.id, screenName = twitter.user.screenName, tabOrder = maxOrder + 2)
                        //    SavedTab(type = MENTION, accountId = twitter.id, screenName = twitter.user.screenName, tabOrder = maxOrder + 3),
                       //     SavedTab(type = NOTIFICATION, accountId = twitter.id, screenName = twitter.user.screenName, tabOrder = maxOrder + 4)
                    ))
                }.await()

            }
        }
    }

    fun initNotification() {
        if (Build.VERSION.SDK_INT >= 26 && !getApplication<Roselin>().defaultSharedPreferences.getBoolean("notification_initialized", false)) {
            val channel = arrayListOf(
                    NotificationChannel(
                            "reply",
                            "リプライ通知",
                            NotificationManager.IMPORTANCE_DEFAULT
                    ), NotificationChannel(
                    "sending",
                    "送信中通知",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            )
            getApplication<Roselin>().getNotificationManager().createNotificationChannels(channel)
            getApplication<Roselin>().defaultSharedPreferences.putBoolean("notification_initialized", true)
        }
    }

    //SendTweet
    fun sendTweet(text: String) {
        if (!text.isBlank() && text.codePointCount(0, text.length) <= 140) {
            launch(UI) {
                try {
                    val status = async(CommonPool) { twitter.account.updateStatus(text) }.await()
                    postSucceed.value = status
                } catch (e: Exception) {
                    e.printStackTrace()
                    postSucceed.value = null
                }
            }
        }
    }

    fun deleteTweet(id: Long) {
        launch(UI) {
            try {
                async(CommonPool) { twitter.account.destroyStatus(id) }.await()
                deleteSucceed.call()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //User
    val user: MutableLiveData<User> = MutableLiveData()

    fun initUser() {
        if (user.value == null)
            launch(UI) {
                try {
                    user.value = async { twitter.account.verifyCredentials() }.await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
    }

    //InitStream
    fun initStream() {
        val app: Roselin = getApplication()
        if (app.defaultSharedPreferences.getBoolean("use_home_stream", true)) {
            app.startService<StreamingService>()
        }
        //  app.startService<SearchStreamService>()
    }

    //Destroy
    override fun onCleared() {
        super.onCleared()
        val app: Roselin = getApplication()
        LocalBroadcastManager.getInstance(app).apply {
            unregisterReceiver(connectionReceiver)
        }
        app.unregisterReceiver(receiver)
        app.unregisterReceiver(sendReplyReceiver)

        app.stopService(app.newIntent<SearchStreamService>())
        app.stopService(app.newIntent<StreamingService>())

    }


    fun getAlbumart(context: Context, album_id: Long): Bitmap? {
        return if (album_id > 0) {
            var bm: Bitmap? = null
            val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
            val options = BitmapFactory.Options()
            val uri = ContentUris.withAppendedId(sArtworkUri, album_id)
            context.contentResolver.openFileDescriptor(uri, "r").use {
                it?.let {
                    bm = BitmapFactory.decodeFileDescriptor(it.fileDescriptor, null, options)
                }
            }
            bm
        } else null
    }
}

class SendReplyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val text = getMessageText(intent) as String?
        val twitter by lazy { getAccount() }
        val screenname = intent.getStringExtra("screen_name")
        val statusId = intent.getLongExtra("status_id", 0L)

        launch(UI) {
            try {
                async(CommonPool) {
                    twitter.account.updateStatus(StatusUpdate("@$screenname  $text").apply {
                        inReplyToStatusId = statusId
                    })
                }.await()
                Log.v("test", text.toString())
                repliedNotification(context, "送信しました")
                Handler().delayed(2000, {
                    context.getNotificationManager().cancel(REPLY_ID)
                })
            } catch (e: Exception) {
                Log.v("test", "No message.")
                repliedNotification(context, "失敗しました")
            }

        }
    }

    private fun repliedNotification(context: Context, reply_text: String) {
        val repliedNotification = context.newNotification({
            setSmallIcon(R.drawable.ic_reply)
            setContentText(reply_text)

        }, "Reply")
        context.getNotificationManager().notify(REPLY_ID, repliedNotification)
    }

    private fun getMessageText(intent: Intent) =
            RemoteInput.getResultsFromIntent(intent)?.getCharSequence("key_reply")
}