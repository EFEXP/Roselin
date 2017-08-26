package xyz.donot.roselin.service

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import twitter4j.*
import xyz.donot.roselin.util.*


class StreamService : IntentService("StreamService") {
    private val twitter by lazy {  getTwitterInstance()}
    private val stream: TwitterStream by lazy {  TwitterStreamFactory().getInstance(twitter.authorization) }

    override fun onHandleIntent(intent: Intent?) = handleActionStream()
    private fun handleActionStream(){
        StreamCreateUtil.addStatusListener(stream,MyStreamAdapter())
        stream.addConnectionLifeCycleListener(MyConnectionListener())
        stream.user()
    }


    inner class MyConnectionListener:ConnectionLifeCycleListener{
        override fun onConnect() {
            LocalBroadcastManager.getInstance(this@StreamService).sendBroadcast(Intent("OnConnect"))
        }

        override fun onCleanUp() {
            LocalBroadcastManager.getInstance(this@StreamService).sendBroadcast(Intent("OnCleanUp"))
        }

        override fun onDisconnect() {
            LocalBroadcastManager.getInstance(this@StreamService).sendBroadcast(Intent("OnDisconnect"))
        }
    }
    inner class MyStreamAdapter: UserStreamAdapter(){

            override fun onStatus(status: Status) {
                if(status.isRetweet)
                {
                    if(status.retweetedStatus.user.id== getMyId()){
                        LocalBroadcastManager.getInstance(this@StreamService).sendBroadcast(Intent("onRetweeted").putExtra("Status", status.getSerialized()))
                    }

                }
                else{
                    if (status.inReplyToUserId== getMyId())  LocalBroadcastManager.getInstance(this@StreamService).sendBroadcast(Intent("NewReply").putExtra("Status", status.getSerialized()))
                }
                if (canPass(status)){
                LocalBroadcastManager.getInstance(this@StreamService).sendBroadcast(Intent("NewStatus").putExtra("Status", status.getSerialized()))}
            }

        override fun onException(ex: Exception) {
            super.onException(ex)
            ex.printStackTrace()
        }

        override fun onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {
            LocalBroadcastManager.getInstance(this@StreamService).sendBroadcast(Intent("DeleteStatus").putExtra("StatusDeletionNotice",statusDeletionNotice.getSerialized()))
        }

        override fun onFavorite(source: User, target: User, favoritedStatus: Status) {
            super.onFavorite(source, target, favoritedStatus)
            LocalBroadcastManager.getInstance(this@StreamService)
                    .sendBroadcast(Intent("OnFavorited")
                            .putExtra("Status",favoritedStatus.getSerialized()))
        }
    }
}

