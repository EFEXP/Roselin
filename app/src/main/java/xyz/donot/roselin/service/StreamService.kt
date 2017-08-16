package xyz.donot.roselin.service

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import twitter4j.*
import xyz.donot.roselin.R
import xyz.donot.roselin.util.StreamCreateUtil
import xyz.donot.roselin.util.extraUtils.logd
import xyz.donot.roselin.util.getSerialized
import xyz.donot.roselin.util.getTwitterInstance


class StreamService : IntentService("StreamService") {
    val twitter= getTwitterInstance()

    override fun onHandleIntent(intent: Intent?) {
        val mNotification = NotificationCompat.Builder(this,"Stream")
                 .setSmallIcon(R.drawable.tw__ic_logo_default)
                .setContentTitle("Streaming")
                .setAutoCancel(false)
                .setContentText("ストリームは正常に稼働中です。")
                .build()
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(0, mNotification )
        handleActionStream()
    }
    private fun handleActionStream(){
        val stream = TwitterStreamFactory().getInstance(twitter.authorization)
        StreamCreateUtil.addStatusListener(stream,MyStreamAdapter())
        stream.user()
    }

    override fun onDestroy() {
        super.onDestroy()

    }
    inner class MyStreamAdapter: UserStreamAdapter(){

            override fun onStatus(x: Status) {
                logd { "OnStatus" }
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
        }
    }
}

