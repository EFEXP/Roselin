package xyz.donot.roselinx.viewmodel.activity

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
import io.realm.Realm
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Status
import twitter4j.StatusUpdate
import twitter4j.User
import xyz.donot.roselinx.R
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.model.realm.*
import xyz.donot.roselinx.service.REPLY_ID
import xyz.donot.roselinx.service.SearchStreamService
import xyz.donot.roselinx.service.StreamingService
import xyz.donot.roselinx.util.extraUtils.*
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.util.getMyScreenName
import xyz.donot.roselinx.util.getTwitterInstance
import xyz.donot.roselinx.view.custom.SingleLiveEvent


class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var receiver: BroadcastReceiver? = null
    private val disConnectionReceiver by lazy { DisConnectionReceiver() }
    private val connectionReceiver by lazy { ConnectionReceiver() }
    private val sendReplyReceiver by lazy { SendReplyReceiver() }
    private val twitter by lazy { getTwitterInstance() }
    val isConnectedStream = MutableLiveData<Boolean>()
    val postSucceed = MutableLiveData<Status>()
    val deleteSucceed = SingleLiveEvent<Unit>()
    fun registerReceivers() {
        val app: Roselin = getApplication()
        LocalBroadcastManager.getInstance(app).apply {
            registerReceiver(disConnectionReceiver, IntentFilter("OnDisconnect"))
            registerReceiver(connectionReceiver, IntentFilter("OnConnect"))
        }
        val intentFilter = IntentFilter().apply {
            addAction("com.android.music.metachanged")
            addAction("com.android.music.playstatechanged")
            addAction("com.android.music.playbackcomplete")
        }
        receiver = MusicReceiver()
        app.registerReceiver(receiver, intentFilter)
        app.registerReceiver(sendReplyReceiver, IntentFilter())
    }

    inner class ConnectionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            isConnectedStream.value = true
        }
    }

    inner class DisConnectionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            isConnectedStream.value = false
        }
    }

    inner class MusicReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            //  val app:Roselin=getApplication()
            // val t=  getAlbumart(app, bundle.getLong("id",-1))
            //   if (t!=null){
            //       app.toast("Good Work!")
            //   }
            context.defaultSharedPreferences.edit().apply {
                putString("track", bundle.getString("track"))
                putString("artist", bundle.getString("artist"))
                putString("album", bundle.getString("album"))
            }.apply()

        }
    }

    private val realm = Realm.getDefaultInstance()
    //realm
    fun initTab() {
        if (realm.where(TabDataObject::class.java).count() <= 0) {
            realm.executeTransaction {
                it.createObject(TabDataObject::class.java).apply {
                    order = 0
                    type = SETTING
                }
                it.createObject(TabDataObject::class.java).apply {
                    order = 1
                    type = HOME
                    accountId = getMyId()
                    screenName = getMyScreenName()
                }
                it.createObject(TabDataObject::class.java).apply {
                    order = 3
                    type = NOTIFICATION
                    accountId = getMyId()
                    screenName = getMyScreenName()
                }
                it.createObject(TabDataObject::class.java).apply {
                    order = 2
                    type = MENTION
                    accountId = getMyId()
                    screenName = getMyScreenName()
                }
            }
        }
    }
    fun initNotification(){
        if (Build.VERSION.SDK_INT  >= 26&&!getApplication<Roselin>().defaultSharedPreferences.getBoolean("notification_initialized",false)) {
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
            getApplication<Roselin>().defaultSharedPreferences.putBoolean("notification_initialized",true)
        }
    }

    //SendTweet
    fun sendTweet(text: String) {
        if (!text.isBlank() && text.codePointCount(0, text.length) <= 140) {
            launch(UI) {
                try {
                    val status = async(CommonPool) { getTwitterInstance().updateStatus(text) }.await()
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
                async(CommonPool) { twitter.destroyStatus(id) }.await()
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
                user.value = async(CommonPool) { twitter.verifyCredentials() }.await()
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
            unregisterReceiver(disConnectionReceiver)
        }
        if (receiver != null) {
            app.unregisterReceiver(receiver)
        }
        if (receiver != sendReplyReceiver) {
            app.unregisterReceiver(sendReplyReceiver)
        }
        app.stopService(app.newIntent<SearchStreamService>())
        app.stopService(app.newIntent<StreamingService>())
        realm.close()
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
        val screenname = intent.getStringExtra("screen_name")
        val statusId = intent.getLongExtra("status_id", 0L)
        launch(UI) {
            try {
                async(CommonPool) {
                    getTwitterInstance().updateStatus(StatusUpdate("@$screenname  $text").apply {
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