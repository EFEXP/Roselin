package xyz.donot.roselinx.service

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import io.realm.Realm
import twitter4j.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.realm.DBNotification
import xyz.donot.roselinx.model.realm.NFAVORITE
import xyz.donot.roselinx.model.realm.NRETWEET
import xyz.donot.roselinx.util.*
import xyz.donot.roselinx.util.extraUtils.*


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

	override fun stopService(name: Intent?): Boolean = super.stopService(name)

	override fun onCreate() {
		super.onCreate()
		try {
			handleActionStream()
		} catch (e: Exception) {
			twitterExceptionToast(e)
		}
	}

	override fun startService(service: Intent?): ComponentName = super.startService(service)
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
			LocalBroadcastManager.getInstance(this@StreamingService).sendBroadcast(Intent("NewMessage").putExtra("Status",directMessage.getSerialized()))
		}

		override fun onStatus(onStatus: Status) {
			if (onStatus.isRetweet) {
				//RT
				if (onStatus.retweetedStatus.user.id == getMyId()) {
					if (defaultSharedPreferences.getBoolean("notification_retweet", true)) toast("${onStatus.user.name}にRTされました")
						Realm.getDefaultInstance().use {
							realm->
							realm.executeTransaction {
								it.createObject(DBNotification::class.java).apply {
									status = onStatus.getSerialized()
									sourceUser = onStatus.user.getSerialized()
									type = NRETWEET
								}
							}
					}
				}
			} else {
				if (onStatus.inReplyToUserId == getMyId()) {
					if (defaultSharedPreferences.getBoolean("notification_reply", true)) replyNotification(onStatus)
					LocalBroadcastManager.getInstance(this@StreamingService).sendBroadcast(Intent("NewReply").putExtra("Status", onStatus.getSerialized()))
				}
			}
			if (canPass(onStatus)) {
				LocalBroadcastManager.getInstance(this@StreamingService).sendBroadcast(Intent("NewStatus").putExtra("Status", onStatus.getSerialized()))
			}
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
				 Realm.getDefaultInstance().use {
					 realm->
					 realm.executeTransaction {
						 it.createObject(DBNotification::class.java).apply {
							 status = favoritedStatus.getSerialized()
							 sourceUser = source.getSerialized()
							 type = NFAVORITE
						 }
					 }
				 }

			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		stream.clearListeners()
	}

	fun replyNotification(onStatus: Status) {
		val notification = newNotification {
			setSmallIcon(R.drawable.wrap_reply)
			setStyle(NotificationCompat.BigTextStyle().setSummaryText("会話"))
			setGroup(REPLY_GROUP_KEY)
			setGroupSummary(true)
			setContentTitle("${onStatus.user.name}からリプライ")
			setSound(Uri.parse(defaultSharedPreferences.getString("notifications_ringtone", "")))
			setContentText(onStatus.text)
		}
		getNotificationManager().notify(REPLY_ID, notification)
	}

}
