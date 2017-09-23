package xyz.donot.roselinx.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.RemoteInput
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import io.realm.Realm
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.realm.NFAVORITE
import xyz.donot.roselinx.model.realm.NRETWEET
import xyz.donot.roselinx.model.realm.NotificationObject
import xyz.donot.roselinx.util.*
import xyz.donot.roselinx.util.extraUtils.*
import xyz.donot.roselinx.view.activity.MainActivity
import xyz.donot.roselinx.viewmodel.activity.SendReplyReceiver
import kotlin.concurrent.thread


const val REPLY_ID = 10
const val REPLY_GROUP_KEY = "Reply"

class StreamingService : Service() {
    private val twitter by lazy { getTwitterInstance() }
    private val stream: TwitterStream by lazy { TwitterStreamFactory().getInstance(twitter.authorization) }
    private fun handleActionStream() {
        StreamCreateUtil.addStatusListener(stream, MyStreamAdapter())
        stream.addConnectionLifeCycleListener(MyConnectionListener())
        stream.user()
    }

    override fun onCreate() {
        super.onCreate()
        try {
            handleActionStream()
        } catch (e: TwitterException) {
            toast(twitterExceptionMessage(e))
        }
    }

    override fun onBind(intent: Intent): IBinder? = null
    inner class MyConnectionListener : ConnectionLifeCycleListener {
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

    inner class MyStreamAdapter : UserStreamAdapter() {
        override fun onDirectMessage(directMessage: DirectMessage) {
            super.onDirectMessage(directMessage)
            LocalBroadcastManager.getInstance(this@StreamingService).sendBroadcast(Intent("NewMessage").putExtra("Status", directMessage.getSerialized()))
        }

        override fun onStatus(onStatus: Status) {
            //通知用
            if (onStatus.isRetweet) {
                //RT
                if (onStatus.retweetedStatus.user.id == getMyId()) {
                    if (defaultSharedPreferences.getBoolean("notification_retweet", true)) toast("${onStatus.user.name}にRTされました")
                    mainThread {
                        val realm = Realm.getDefaultInstance()
                        realm.executeTransaction {
                            it.createObject(NotificationObject::class.java).apply {
                                status = onStatus.getSerialized()
                                sourceUser = onStatus.user.getSerialized()
                                type = NRETWEET
                            }
                        }
                    }
                }
            }
            else {
                //通知用
                if (onStatus.inReplyToUserId == getMyId()) {
                    if (defaultSharedPreferences.getBoolean("notification_reply", true)) replyNotification(onStatus)
                    LocalBroadcastManager.getInstance(this@StreamingService).sendBroadcast(Intent("NewReply").putExtra("Status", onStatus.getSerialized()))
                }
                if (canPass(onStatus)) {
                    LocalBroadcastManager.getInstance(this@StreamingService).sendBroadcast(Intent("NewStatus").putExtra("Status", onStatus.getSerialized()))
                    if (onStatus.inReplyToStatusId>0&&!onStatus.isRetweet)
                    {
                        launch(UI){
                            val result= async(CommonPool){twitter.showStatus(onStatus.inReplyToStatusId)}.await()
                            LocalBroadcastManager.getInstance(this@StreamingService).sendBroadcast(Intent("NewStatus").putExtra("Status",result.getSerialized()))
                        }
                    }
                }

            }
            //TLに通すか



        }

        override fun onException(ex: Exception) {
            super.onException(ex)
            ex.printStackTrace()
        }

        override fun onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {
            LocalBroadcastManager.getInstance(this@StreamingService).sendBroadcast(Intent("DeleteStatus").putExtra("StatusDeletionNotice", statusDeletionNotice.getSerialized()))
        }

        override fun onFavorite(source: User, target: User, favoritedStatus: Status) {
            super.onFavorite(source, target, favoritedStatus)
            if (source.id != getMyId()) {
                if (defaultSharedPreferences.getBoolean("notification_favorite", true)) toast("${source.name}にいいねされました")
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction {
                    it.createObject(NotificationObject::class.java).apply {
                        status = favoritedStatus.getSerialized()
                        sourceUser = source.getSerialized()
                        type = NFAVORITE
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stream.clearListeners()
        try {
            thread {
                Runnable {
                    stream.cleanUp()
                    stream.shutdown()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun replyNotification(onStatus: Status) {
        val notification = if (version >= 24) {
            newNotification({
                val activityIntent = Intent(this@StreamingService,MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
               val pendingIntent = PendingIntent.getActivity(this@StreamingService, 0, activityIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT)

                val sendReplyIntent=Intent(this@StreamingService, SendReplyReceiver::class.java).apply {
                    putExtra("screen_name",onStatus.user.screenName)
                    putExtra("status_id",onStatus.id)
                }
                val replyPendingIntent = PendingIntent.getBroadcast(this@StreamingService, 0, sendReplyIntent,PendingIntent.FLAG_CANCEL_CURRENT)
                val remoteInput = RemoteInput.Builder("key_reply")
                        .setLabel("返信")
                        .build()
                val action = NotificationCompat.Action.Builder(R.drawable.ic_send_white_24dp,
                        "ここで返信", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build()
                setContentIntent(pendingIntent)
                setStyle(NotificationCompat.BigTextStyle().setSummaryText("会話"))
                setSmallIcon(R.drawable.wrap_reply)
                setSound(Uri.parse(defaultSharedPreferences.getString("notifications_ringtone", "")))
                setContentTitle("${onStatus.user.name}からリプライ")
                setContentText(onStatus.text)
                addAction(action)
            }, "Reply")
        } else
            newNotification({
                val intent = Intent(this@StreamingService,MainActivity::class.java).apply {
                    putExtra("text", "Notification Activity")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent = PendingIntent.getActivity(this@StreamingService, 0, intent,
                        PendingIntent.FLAG_CANCEL_CURRENT)
                setSmallIcon(R.drawable.wrap_reply)
                setStyle(NotificationCompat.BigTextStyle().setSummaryText("会話"))
                setGroup(REPLY_GROUP_KEY)
                color = ContextCompat.getColor(applicationContext, R.color.colorPrimary)
                setGroupSummary(true)
                setContentTitle("${onStatus.user.name}からリプライ")
                setSound(Uri.parse(defaultSharedPreferences.getString("notifications_ringtone", "")))
                setContentIntent(pendingIntent)
                setContentText(onStatus.text)
            }, "Reply")
        getNotificationManager().notify(REPLY_ID, notification)
    }

}
