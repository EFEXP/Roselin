package xyz.donot.roselinx.ui.home

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.*
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.model.entity.HOME_TIMELINE
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.model.entity.Tweet
import xyz.donot.roselinx.ui.util.extraUtils.logd
import xyz.donot.roselinx.ui.util.extraUtils.toast
import xyz.donot.roselinx.ui.util.extraUtils.twitterExceptionMessage
import xyz.donot.roselinx.ui.util.getAccount
import xyz.donot.roselinx.ui.util.getDeserialized
import xyz.donot.roselinx.ui.view.SingleLiveEvent
import kotlin.properties.Delegates


class HomeTimelineViewModel(app: Application) : AndroidViewModel(app) {
    private val receiver by lazy { StatusReceiver() }
    private val deleteReceiver by lazy { DeleteReceiver() }
    var twitter by Delegates.notNull<Twitter>()
    val mainTwitter by lazy { getAccount() }
    val dataRefreshed = SingleLiveEvent<Unit>()


    var page: Int = 0
        get() {
            field++
            return field
        }


    fun pullDown() {
        launch(UI) {
            try {
                val newestId = async { RoselinDatabase.getInstance().tweetDao().getNewestTweet(HOME_TIMELINE, getAccount().id).tweetId}.await()
                async { twitter.getHomeTimeline(Paging(newestId)) }.await()?.let { Tweet.save(it, HOME_TIMELINE, getAccount().id) }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                dataRefreshed.call()
            }

        }
    }
    fun loadMoreData(hasData: Boolean) {
        launch(UI) {
            try {
                val paging = Paging(page)
                if (hasData) {
                    val oldestId = async { RoselinDatabase.getInstance().tweetDao().getOldestTweet(HOME_TIMELINE, getAccount().id).tweetId }.await()
                    paging.maxId = oldestId
                }
                val result = async { twitter.getHomeTimeline(paging) }.await()
                Tweet.save(result, HOME_TIMELINE,twitter.id)
            } catch (e: TwitterException) {
                //  adapter.loadMoreFail()
                getApplication<Roselin>().toast(twitterExceptionMessage(e))
            }
        }
    }

    fun initService() {
        LocalBroadcastManager.getInstance(getApplication()).apply {
            registerReceiver(receiver, IntentFilter("NewStatus"))
            registerReceiver(deleteReceiver, IntentFilter("DeleteStatus"))
        }
    }

    override fun onCleared() {
        super.onCleared()
        LocalBroadcastManager.getInstance(getApplication()).apply {
            unregisterReceiver(receiver)
            unregisterReceiver(deleteReceiver)
        }
    }

    inner class StatusReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val data = intent.extras.getByteArray("Status").getDeserialized<Status>()
            logd { "onReceived" }
            Tweet.save(data, HOME_TIMELINE,twitter.id)
        }
    }

    inner class DeleteReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val data = intent.extras.getByteArray("StatusDeletionNotice").getDeserialized<StatusDeletionNotice>()
            launch { RoselinDatabase.getInstance().tweetDao().deleteById(data.statusId) }
        }
    }

}