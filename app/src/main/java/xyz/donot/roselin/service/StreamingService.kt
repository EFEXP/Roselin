package xyz.donot.roselin.service

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import io.realm.Realm
import twitter4j.*
import xyz.donot.roselin.model.realm.DBNotification
import xyz.donot.roselin.model.realm.NFAVORITE
import xyz.donot.roselin.model.realm.NRETWEET
import xyz.donot.roselin.util.*
import xyz.donot.roselin.util.extraUtils.logd
import xyz.donot.roselin.util.extraUtils.mainThread

class StreamingService : Service() {
    private val twitter by lazy {  getTwitterInstance() }
    private val stream: TwitterStream by lazy {  TwitterStreamFactory().getInstance(twitter.authorization) }
    private fun handleActionStream(){
        StreamCreateUtil.addStatusListener(stream,MyStreamAdapter())
        stream.addConnectionLifeCycleListener(MyConnectionListener())
        stream.user()
    }
    override fun stopService(name: Intent?): Boolean = super.stopService(name)

    override fun onCreate() {
        logd { "Create" }
        handleActionStream()
        super.onCreate()
    }

    override fun startService(service: Intent?): ComponentName = super.startService(service)
    override fun onBind(intent: Intent): IBinder? = null
    inner class MyConnectionListener:ConnectionLifeCycleListener{
        override fun onConnect() {
            LocalBroadcastManager.getInstance(this@StreamingService).sendBroadcast(Intent("OnConnect"))
        }

        override fun onCleanUp() {
            LocalBroadcastManager.getInstance(this@StreamingService).sendBroadcast(Intent("OnCleanUp"))
        }

        override fun onDisconnect() {
            LocalBroadcastManager.getInstance(this@StreamingService).sendBroadcast(Intent("OnDisconnect"))
        }
    }
    inner class MyStreamAdapter: UserStreamAdapter(){

        override fun onStatus(onStatus: Status) {
            if(onStatus.isRetweet)
            {
                if(onStatus.retweetedStatus.user.id== getMyId()){
                    val realm= Realm.getDefaultInstance()
                    mainThread {
                        realm.executeTransaction {
                            it.createObject(DBNotification::class.java).apply {
                                status=onStatus.getSerialized()
                                sourceUser=onStatus.user.getSerialized()
                                type= NRETWEET } }}
                }}
            else{
                if (onStatus.inReplyToUserId== getMyId())  LocalBroadcastManager.getInstance(this@StreamingService).sendBroadcast(Intent("NewReply").putExtra("Status", onStatus.getSerialized()))
            }
            if (canPass(onStatus)){
                LocalBroadcastManager.getInstance(this@StreamingService).sendBroadcast(Intent("NewStatus").putExtra("Status", onStatus.getSerialized()))}
        }

        override fun onException(ex: Exception) {
            super.onException(ex)
            ex.printStackTrace()
        }

        override fun onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {
            LocalBroadcastManager.getInstance(this@StreamingService).sendBroadcast(Intent("DeleteStatus").putExtra("StatusDeletionNotice",statusDeletionNotice.getSerialized()))
        }

        override fun onFavorite(source: User, target: User, favoritedStatus: Status) {
            super.onFavorite(source, target, favoritedStatus)
            if (source.id!= getMyId()){
                val realm= Realm.getDefaultInstance()
                realm.executeTransaction {
                    it.createObject(DBNotification::class.java).apply {
                        status=favoritedStatus.getSerialized()
                        sourceUser=source.getSerialized()
                        type= NFAVORITE
                    }
                }
            }
        }
    }
}
