package xyz.donot.roselin.service

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import twitter4j.*
import xyz.donot.roselin.util.StreamCreateUtil
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.util.getMyId
import xyz.donot.roselin.util.getSerialized
import xyz.donot.roselin.util.getTwitterInstance


class StreamService : IntentService("StreamService") {
    val twitter= getTwitterInstance()

    override fun onHandleIntent(intent: Intent?) = handleActionStream()
    private fun handleActionStream(){
        val stream = TwitterStreamFactory().getInstance(twitter.authorization)
        StreamCreateUtil.addStatusListener(stream,MyStreamAdapter())
        stream.addConnectionLifeCycleListener(MyConnectionListener())
        stream.user()
    }

    override fun onDestroy() = super.onDestroy()
    inner class MyConnectionListener:ConnectionLifeCycleListener{
        override fun onConnect() {
            toast("onConnect")
            LocalBroadcastManager.getInstance(this@StreamService).sendBroadcast(Intent("OnConnect"))
        }

        override fun onCleanUp() {
            LocalBroadcastManager.getInstance(this@StreamService).sendBroadcast(Intent("OnCleanUp"))
        }

        override fun onDisconnect() {
            toast("onDisConnect")
            LocalBroadcastManager.getInstance(this@StreamService).sendBroadcast(Intent("OnDisconnect"))
        }
    }
    inner class MyStreamAdapter: UserStreamAdapter(){

            override fun onStatus(x: Status) {
                if(x.isRetweet)
                {
                    if(x.retweetedStatus.user.id== getMyId()){
                        LocalBroadcastManager.getInstance(this@StreamService).sendBroadcast(Intent("onRetweeted").putExtra("Status",x.getSerialized()))
                    }

                }
                else{
                    if (x.inReplyToUserId== getMyId())  LocalBroadcastManager.getInstance(this@StreamService).sendBroadcast(Intent("NewReply").putExtra("Status",x.getSerialized()))
                }

                LocalBroadcastManager.getInstance(this@StreamService).sendBroadcast(Intent("NewStatus").putExtra("Status",x.getSerialized()))
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
          //  logd("Favorite",source.name+"to"+target.name )
            //toast(source.name+"to"+target.name )
            LocalBroadcastManager.getInstance(this@StreamService)
                    .sendBroadcast(Intent("OnFavorited")
                            .putExtra("Status",favoritedStatus.getSerialized()))
        }
    }
}

