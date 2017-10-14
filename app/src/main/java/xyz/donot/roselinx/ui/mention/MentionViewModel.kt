package xyz.donot.roselinx.ui.mention

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
import twitter4j.Paging
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.TwitterException
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.model.entity.MENTION_TIMELINE
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.model.entity.Tweet
import xyz.donot.roselinx.ui.util.extraUtils.toast
import xyz.donot.roselinx.ui.util.extraUtils.twitterExceptionMessage
import xyz.donot.roselinx.ui.util.getAccount
import xyz.donot.roselinx.ui.util.getDeserialized
import xyz.donot.roselinx.ui.view.SingleLiveEvent
import kotlin.properties.Delegates

class MentionViewModel(application: Application) : AndroidViewModel(application) {
    private val replyReceiver by lazy { ReplyReceiver() }
    var twitter by Delegates.notNull<Twitter>()
    val mainTwitter by lazy { getAccount() }
    val dataRefreshed = SingleLiveEvent<Unit>()
    var page: Int = 0
        get() {
            field++
            return field
        }

    fun initService() {
        LocalBroadcastManager.getInstance(getApplication()).apply {
            registerReceiver(replyReceiver, IntentFilter("NewReply"))
        }
    }

    fun pullDown() {
        launch(UI) {
            try {
                val newestId = async { RoselinDatabase.getInstance().tweetDao().getNewestTweet(MENTION_TIMELINE,twitter.id).tweetId }.await()
                async { twitter.getMentionsTimeline(Paging(newestId)) }.await()?.let { Tweet.save(it, MENTION_TIMELINE,twitter.id) }
            }
            catch (e:Exception){
                e.printStackTrace()
            }
            finally {
                dataRefreshed.call()
            }

        }
    }

    fun loadMoreData(hasData: Boolean) {
        launch(UI) {
            try {
                val paging = Paging(page)
                if (hasData) {
                    val oldestId = async { RoselinDatabase.getInstance().tweetDao().getOldestTweet(MENTION_TIMELINE, twitter.id).tweetId }.await()
                    paging.maxId = oldestId
                }
                val result = async { twitter.getMentionsTimeline(paging) }.await()
                Tweet.save(result, MENTION_TIMELINE,twitter.id)
            } catch (e: TwitterException) {
                //  adapter.loadMoreFail()
                getApplication<Roselin>().toast(twitterExceptionMessage(e))
            }
        }
    }

    inner class ReplyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val data = intent.extras.getByteArray("Status").getDeserialized<Status>()
            Tweet.save(data, MENTION_TIMELINE,twitter.id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        LocalBroadcastManager.getInstance(getApplication()).apply {
            unregisterReceiver(replyReceiver)
        }

    }
}