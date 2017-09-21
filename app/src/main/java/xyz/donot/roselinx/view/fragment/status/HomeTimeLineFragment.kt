package xyz.donot.roselinx.view.fragment.status

import android.app.Application
import android.arch.lifecycle.ViewModelProviders
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
import xyz.donot.roselinx.util.extraUtils.*
import xyz.donot.roselinx.util.getDeserialized
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.util.getTwitterInstance
import xyz.donot.roselinx.view.activity.EditTweetActivity
import xyz.donot.roselinx.view.activity.TwitterDetailActivity
import xyz.donot.roselinx.view.custom.MyLoadingView
import xyz.donot.roselinx.view.fragment.RetweeterDialog
import xyz.donot.roselinx.view.playground.MainTimeLineFragment
import xyz.donot.roselinx.view.playground.MainTimeLineViewModel
import kotlin.properties.Delegates


class HomeTimeLineFragment : MainTimeLineFragment() {
    override val viewmodel: HomeTimeLineViewModel by lazy { ViewModelProviders.of(this).get(HomeTimeLineViewModel::class.java) }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.apply {
            twitter = arguments.getByteArray("twitter").getDeserialized()
            if (savedInstanceState == null) {
                adapter.apply {
                    setOnLoadMoreListener({ viewmodel.loadMoreData() }, recycler)
                    setLoadMoreView(MyLoadingView())
                    emptyView = View.inflate(activity, R.layout.item_empty, null)
                    initService()
                }
                loadMoreData()
            }
            adapter.setOnItemClickListener { adapter, _, position ->
                val status = adapter.data[position] as Status
                val item = if (status.isRetweet) {
                    status.retweetedStatus
                } else {
                    status
                }
                if (!activity.isFinishing) {
                    val tweetItem = if (getMyId() == status.user.id) {
                        R.array.tweet_my_menu
                    } else {
                        R.array.tweet_menu
                    }
                    AlertDialog.Builder(context).setItems(tweetItem, { _, int ->
                        val selectedItem = context.resources.getStringArray(tweetItem)[int]
                        when (selectedItem) {
                            "返信" -> {
                                xyz.donot.roselinx.util.extraUtils.Bundle {
                                    putString("status_txt", item.text)
                                    putLong("status_id", item.id)
                                    putString("user_screen_name", item.user.screenName)
                                }
                                activity.start<EditTweetActivity>(
                                        xyz.donot.roselinx.util.extraUtils.Bundle {
                                            putString("status_txt", item.text)
                                            putLong("status_id", item.id)
                                            putString("user_screen_name", item.user.screenName)
                                        }
                                )
                            }
                            "削除" -> {
                                launch(UI) {
                                    try {
                                        async(CommonPool) { viewmodel.mainTwitter.destroyStatus(status.id) }.await()
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
                                val rd = RetweeterDialog()
                                rd.arguments = Bundle { putLong("tweetId", item.id) }
                                rd.show(activity.supportFragmentManager, "")
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

class HomeTimeLineViewModel(app: Application) : MainTimeLineViewModel(app) {
    private val receiver by lazy { StatusReceiver() }
    private val deleteReceiver by lazy { DeleteReceiver() }
    var twitter by Delegates.notNull<Twitter>()
    val mainTwitter by lazy { getTwitterInstance() }


    fun pullDown() {
        if (adapter.data.isNotEmpty()) {
            launch(UI) {
                async(CommonPool) { twitter.getHomeTimeline(Paging(adapter.data[0].id)) }.await()?.let { insertDataBackground(it) }
                dataRefreshed.call()
            }
        } else {
            dataRefreshed.call()
        }
    }

    fun loadMoreData() {
        launch(UI) {
            try {
                val result = async(CommonPool) { twitter.getHomeTimeline(Paging(page)) }.await()
                if (result.isEmpty()) {
                    endAdapter()
                } else {
                    adapter.addData(result)
                    adapter.loadMoreComplete()
                }
            } catch (e: TwitterException) {
                adapter.loadMoreFail()
                exception.value = e
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
            insertDataBackground(data)
        }
    }

    inner class DeleteReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val data = intent.extras.getByteArray("StatusDeletionNotice").getDeserialized<StatusDeletionNotice>()
            adapter.data.filter { de -> de.id == data.statusId }.mapNotNull {
                val int = adapter.data.indexOf(it)
                adapter.remove(int)
            }
        }
    }

}

