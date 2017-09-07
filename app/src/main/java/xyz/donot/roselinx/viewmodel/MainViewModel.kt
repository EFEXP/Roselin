package xyz.donot.roselinx.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import io.realm.Realm
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Status
import twitter4j.User
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.model.realm.DBTabData
import xyz.donot.roselinx.model.realm.HOME
import xyz.donot.roselinx.model.realm.MENTION
import xyz.donot.roselinx.model.realm.NOTIFICATION
import xyz.donot.roselinx.service.StreamingService
import xyz.donot.roselinx.util.extraUtils.defaultSharedPreferences
import xyz.donot.roselinx.util.extraUtils.newIntent
import xyz.donot.roselinx.util.extraUtils.startService
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.util.getMyScreenName
import xyz.donot.roselinx.util.getTwitterInstance

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var receiver: BroadcastReceiver? = null
    private val disConnectionReceiver by lazy { DisConnectionReceiver() }
    private val connectionReceiver by lazy { ConnectionReceiver() }
    private val twitter by lazy { getTwitterInstance()}
    val isConnectedStream = MutableLiveData<Boolean>()
    val postSucceed = MutableLiveData<Status>()
    val deleteSucceed = MutableLiveData<Unit>()
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
        if (realm.where(DBTabData::class.java).count() == 0L) {
            realm.executeTransaction {
                it.createObject(DBTabData::class.java).apply {
                    order = 0
                    type = HOME
                    accountId = getMyId()
                    screenName = getMyScreenName()
                }
                it.createObject(DBTabData::class.java).apply {
                    order = 2
                    type = NOTIFICATION
                    accountId = getMyId()
                    screenName = getMyScreenName()
                }
                it.createObject(DBTabData::class.java).apply {
                    order = 1
                    type = MENTION
                    accountId = getMyId()
                    screenName = getMyScreenName()
                }
            }
        }
    }
    //SendTweet
    fun sendTweet(text:String){
        if (!text.isBlank() &&text.count() <= 140) {
            launch(UI) {
                try {
                   val status= async(CommonPool) { getTwitterInstance().updateStatus(text) }.await()
                    postSucceed.value=status
                } catch (e: Exception) {
                   e.printStackTrace()
                    postSucceed.value=null
                }
            }
        }
    }
    fun deleteTweet(id: Long){
            launch(UI) {
                try {
                      async(CommonPool) { twitter.destroyStatus(id) }.await()
                    deleteSucceed.value=Unit
                } catch (e: Exception) {
                    e.printStackTrace()
            }
        }
    }

    //User
     val user: MutableLiveData<User> =MutableLiveData()
    fun initUser() {
        launch(UI){
            user.value=  async(CommonPool){twitter.verifyCredentials()}.await()
        }
    }
    //InitStream
    fun initStream() {
        val app: Roselin = getApplication()
        if (app.defaultSharedPreferences.getBoolean("use_home_stream", true)) {
            app.startService<StreamingService>()
        }
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
        app.stopService(app.newIntent<StreamingService>())
        realm.close()
    }


}
