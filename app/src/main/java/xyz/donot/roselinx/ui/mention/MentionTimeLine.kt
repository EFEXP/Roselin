package xyz.donot.roselinx.ui.mention

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
import twitter4j.Paging
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.TwitterException
import xyz.donot.roselinx.R
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.model.entity.MENTION_TIMELINE
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.model.entity.Tweet
import xyz.donot.roselinx.ui.base.ARecyclerFragment
import xyz.donot.roselinx.ui.detailtweet.TwitterDetailActivity
import xyz.donot.roselinx.ui.dialog.RetweetUserDialog
import xyz.donot.roselinx.ui.editteweet.EditTweetActivity
import xyz.donot.roselinx.ui.status.TweetAdapter
import xyz.donot.roselinx.ui.util.extraUtils.delayed
import xyz.donot.roselinx.ui.util.extraUtils.newIntent
import xyz.donot.roselinx.ui.util.extraUtils.toast
import xyz.donot.roselinx.ui.util.extraUtils.twitterExceptionMessage
import xyz.donot.roselinx.ui.util.getAccount
import xyz.donot.roselinx.ui.util.getDeserialized
import xyz.donot.roselinx.ui.view.SingleLiveEvent
import kotlin.properties.Delegates

class MentionTimeLine : ARecyclerFragment() {
    val viewmodel: MentionViewModel by lazy { ViewModelProviders.of(this).get(MentionViewModel::class.java) }
    val adapter by lazy { TweetAdapter(activity) }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.apply {
            twitter = arguments.getByteArray("twitter").getDeserialized()
            adapter.apply {
                onLoadMore = {
                    viewmodel.loadMoreData(true)
                }
                onItemClick = { (status), _ ->
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
                                    startActivity(EditTweetActivity.newIntent(activity,item.text,item.id, item.user.screenName))
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
                dataRefreshed.observe(this@MentionTimeLine, Observer {
                    refresh.setRefreshing(false)
                })
                //    setOnLoadMoreListener({ viewmodel.loadMoreData() }, recycler)
                //    setLoadMoreView(MyLoadingView())
                //     emptyView = View.inflate(activity, R.layout.item_empty, null)
                /*   addHeaderView(View.inflate(activity, R.layout.item_ad, null).apply {
                       adView.loadAd(AdRequest.Builder()
                               .setGender(AdRequest.GENDER_MALE)
                               .addTestDevice("0CF83648F3E630518CF53907939C9A8D")
                               .addTestDevice("6D38172C5A30A07095F6420BC145C497")
                               .build())
                   })*/
            }
            if (savedInstanceState == null) {
                initService()
            }
            launch(UI) {
                async {
                    RoselinDatabase.getInstance().tweetDao().getAllDataSource(MENTION_TIMELINE,twitter.id)
                            .create(0, PagedList.Config.Builder().setPageSize(10).setPrefetchDistance(50).build()) }.await()
                        .observe(this@MentionTimeLine, Observer {
                            it?.let {
                                if (it.isEmpty())
                                    viewmodel.loadMoreData(false)
                                adapter.setList(it)
                            }
                        })
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

