package xyz.donot.roselinx.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.view.View
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.model.entity.HOME_TIMELINE
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.model.entity.Tweet
import xyz.donot.roselinx.ui.base.ARecyclerFragment
import xyz.donot.roselinx.ui.detailtweet.TwitterDetailActivity
import xyz.donot.roselinx.ui.dialog.RetweetUserDialog
import xyz.donot.roselinx.ui.editteweet.EditTweetActivity
import xyz.donot.roselinx.ui.status.TweetAdapter
import xyz.donot.roselinx.ui.util.getAccount
import xyz.donot.roselinx.ui.util.getDeserialized
import xyz.donot.roselinx.ui.view.SingleLiveEvent
import xyz.donot.roselinx.util.extraUtils.*
import kotlin.properties.Delegates

class HomeTimeLineFragment : ARecyclerFragment() {
    val viewmodel: HomeTimeLineViewModel by lazy { ViewModelProviders.of(this).get(HomeTimeLineViewModel::class.java) }
    val adapter by lazy { TweetAdapter(activity) }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.apply {
            twitter = arguments.getByteArray("twitter").getDeserialized()
            adapter.apply {
                onLoadMore = { viewmodel.loadMoreData(true) }
                adapter.onItemClick = { (status), _ ->
                    val item = if (status.isRetweet) {
                        status.retweetedStatus
                    } else {
                        status
                    }
                    if (!activity.isFinishing) {
                        val tweetItem = if (mainTwitter.id == status.user.id) {
                            R.array.tweet_my_menu
                        } else {
                            R.array.tweet_menu
                        }
                        AlertDialog.Builder(context).setItems(tweetItem, { _, int ->
                            val selectedItem = context.resources.getStringArray(tweetItem)[int]
                            when (selectedItem) {
                                "返信" -> {
                                    Bundle {
                                        putString("status_txt", item.text)
                                        putLong("status_id", item.id)
                                        putString("user_screen_name", item.user.screenName)
                                    }
                                    activity.start<EditTweetActivity>(
                                            Bundle {
                                                putString("status_txt", item.text)
                                                putLong("status_id", item.id)
                                                putString("user_screen_name", item.user.screenName)
                                            }
                                    )
                                }
                                "削除" -> {
                                    launch(UI) {
                                        try {
                                            async(CommonPool) { viewmodel.mainTwitter.account.destroyStatus(status.id) }.await()
                                            toast("削除しました")
                                        } catch (e: Exception) {
                                            toast(e.localizedMessage)
                                        }
                                    }

                                }
                                "会話" -> {
                                    context.startActivity(context.newIntent<TwitterDetailActivity>(Bundle().apply { putSerializable("Status", item) }))

                                }
                                "コピー" -> {
                                    (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip = ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_URILIST, item.text)
                                    toast("コピーしました")

                                }
                                "RTした人" -> {
                                    RetweetUserDialog.getInstance(item.id).show(childFragmentManager, "")
                                }
                                "共有" -> {
                                    context.startActivity(Intent().apply {
                                        action = Intent.ACTION_SEND
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, "@${item.user.screenName}さんのツイート https://twitter.com/${item.user.screenName}/status/${item.id}をチェック")
                                    })
                                }
                                "公式で見る" -> {
                                    CustomTabsIntent.Builder()
                                            .setShowTitle(true)
                                            .addDefaultShareMenuItem()
                                            .setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
                                            .setStartAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                            .setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right).build()
                                            .launchUrl(context, Uri.parse("https://twitter.com/${item.user.screenName}/status/${item.id}"))
                                }
                            }
                        }).show()
                    }
                }
                dataRefreshed.observe(this@HomeTimeLineFragment, Observer {
                    refresh.setRefreshing(false)
                })
            }
            if (savedInstanceState == null) {
                initService()
            }
            launch(UI) {
                async {
                    RoselinDatabase.getInstance().tweetDao().getAllDataSource(HOME_TIMELINE,twitter.id)
                            .create(0, PagedList.Config.Builder().setPageSize(50).setPrefetchDistance(50).build()) }.await()
                        .observe(this@HomeTimeLineFragment, Observer {
                            it?.let {
                                adapter.setList(it)
                            }
                        })
            }
            viewmodel.loadMoreData(false)
            recycler.adapter = adapter
            refresh.setOnRefreshListener {
                Handler().delayed(1000, {
                    pullDown()
                })
            }

        }
        refresh.isEnabled = true
    }
}

class HomeTimeLineViewModel(app: Application) : AndroidViewModel(app) {
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
                async { twitter.getHomeTimeline(Paging(newestId)) }.await()?.let { Tweet.save(it, HOME_TIMELINE,getAccount().id) }
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
                    val oldestId = async { RoselinDatabase.getInstance().tweetDao().getOldestTweet(HOME_TIMELINE,getAccount().id).tweetId }.await()
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
