package xyz.donot.roselinx.view.fragment.status

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Query
import twitter4j.Status
import twitter4j.TwitterException
import xyz.donot.roselinx.R
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.util.extraUtils.*
import xyz.donot.roselinx.util.getAccount
import xyz.donot.roselinx.util.getDeserialized
import xyz.donot.roselinx.view.activity.EditTweetActivity
import xyz.donot.roselinx.view.activity.TwitterDetailActivity
import xyz.donot.roselinx.view.adapter.StatusAdapter
import xyz.donot.roselinx.view.custom.MyLoadingView
import xyz.donot.roselinx.view.custom.SingleLiveEvent
import xyz.donot.roselinx.view.fragment.base.ARecyclerFragment
import xyz.donot.roselinx.view.fragment.user.RetweetUserDialog

class SearchTimeline : ARecyclerFragment() {
    val viewmodel: SearchViewModel by lazy { ViewModelProviders.of(this).get(SearchViewModel::class.java) }
    val account by lazy { getAccount() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel.query.value = arguments.getByteArray("query_bundle").getDeserialized<Query>()
    }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.apply {
            adapter.apply {
                        setOnLoadMoreListener({ viewmodel.loadMoreData() }, recycler)
                        setLoadMoreView(MyLoadingView())
                        emptyView = View.inflate(activity, R.layout.item_empty, null)
                        setOnItemClickListener { adapter, _, position ->
                            val status = adapter.data[position] as Status
                            val item = if (status.isRetweet) {
                                status.retweetedStatus
                            } else {
                                status
                            }
                            if (!(context as Activity).isFinishing) {
                                val tweetItem = if (account.id== status.user.id) {
                                    R.array.tweet_my_menu
                                } else {
                                    R.array.tweet_menu
                                }
                                AlertDialog.Builder(context)
                                        .setItems(tweetItem, { _, int ->
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
                    }
            recycler.adapter = adapter
            refresh.isEnabled = true

            if (savedInstanceState == null)
            viewmodel.loadMoreData()
            viewmodel.exception.observe(this@SearchTimeline, Observer {
                it?.let {
                    adapter.emptyView = View.inflate(activity, R.layout.item_no_content, null)
                }
            })
            dataRefreshed.observe(this@SearchTimeline, Observer {
                refresh.setRefreshing(false)
            })

            /*  adapter.addHeaderView(
                      View.inflate(activity, R.layout.item_ad, null).apply {
                          adView.loadAd(AdRequest.Builder()
                                  .setGender(AdRequest.GENDER_MALE)
                                  .addTestDevice("0CF83648F3E630518CF53907939C9A8D")
                                  .addTestDevice("6D38172C5A30A07095F6420BC145C497")
                                  .build())
                      }
              )*/

            refresh.setOnRefreshListener {
                Handler().delayed(1000, {
                    pullDown()
                })
            }
        }
    }

}


class SearchViewModel(application: Application) : AndroidViewModel(application) {
    var query: MutableLiveData<Query> = MutableLiveData()
    val exception = MutableLiveData<TwitterException>()
    val mainTwitter by lazy { getAccount() }
    val dataRefreshed = SingleLiveEvent<Unit>()
    val adapter: BaseQuickAdapter<Status, BaseViewHolder> by lazy { StatusAdapter() }
    val data = MutableLiveData<List<Status>>()
    fun pullDown() {
        if (adapter.data.isNotEmpty()) {
            launch(UI) {
                adapter.data.clear()
                adapter.notifyDataSetChanged()
                loadMoreData()
                dataRefreshed.call()
            }
        } else {
            dataRefreshed.call()
        }
    }

    private fun endAdapter() = mainThread {
        adapter.loadMoreEnd(true)
    }

    fun loadMoreData() {
        launch(UI) {
            try {
                val result = async(CommonPool) {mainTwitter.account.search(query.value) }.await()
                if (result.hasNext()) {
                    query.value = result.nextQuery()
                    adapter.loadMoreComplete()
                } else {
                    endAdapter()
                }
                adapter.addData(result.tweets)
            } catch (e: TwitterException) {
                getApplication<Roselin>().toast(twitterExceptionMessage(e))

            }
        }

    }
}