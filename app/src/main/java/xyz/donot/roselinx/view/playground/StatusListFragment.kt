package xyz.donot.roselinx.view.playground

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.TwitterException
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.*
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.util.getTwitterInstance
import xyz.donot.roselinx.view.activity.EditTweetActivity
import xyz.donot.roselinx.view.activity.TwitterDetailActivity
import xyz.donot.roselinx.view.adapter.StatusAdapter
import xyz.donot.roselinx.view.custom.SingleLiveEvent
import xyz.donot.roselinx.view.fragment.ARecyclerFragment
import xyz.donot.roselinx.view.fragment.MathDialog
import xyz.donot.roselinx.view.fragment.RetweeterDialog
import kotlin.properties.Delegates

abstract class MainTimeLineViewModel(app: Application) : AndroidViewModel(app) {
    val adapter by lazy { StatusAdapter() }
    var twitter by Delegates.notNull<Twitter>()
    val mainTwitter by lazy { getTwitterInstance() }
    val dataInserted = SingleLiveEvent<Unit>()
    val dataRefreshed = SingleLiveEvent<Unit>()
    private val dataStore: ArrayList<Status> = ArrayList()
    val exception = MutableLiveData<TwitterException>()
    protected var page: Int = 0
        get() {
            field++
            return field
        }

    fun insertDataBackground(data: List<Status>) = mainThread {
        mainThread {
            if (isBackground) {
                dataStore.addAll(0, data)
            } else {
                adapter.addData(0, data)
                dataInserted.call()
            }
        }
    }

    fun endAdapter() = mainThread {
        adapter.loadMoreEnd(true)
    }
    var isBackground = false
        set(value) {
            if (!value)
                if (dataStore.isNotEmpty()) {
                    adapter.addData(0, dataStore)
                    dataStore.clear()
                    dataInserted.call()
                }
        }

    fun insertDataBackground(data: Status) = mainThread {
        mainThread {
            if (isBackground) {
                dataStore.add(0, data)
            } else {
                adapter.addData(0, data)
                dataInserted.call()
            }
        }
    }

}

abstract class MainTimeLineFragment: ARecyclerFragment(){
    abstract val viewmodel:MainTimeLineViewModel
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel.apply {
            if (savedInstanceState == null)
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
                            "数式" -> {
                                MathDialog().apply {
                                    arguments=Bundle { putString("math_formula",item.text) }
                                }.show(activity.supportFragmentManager,"")
                            }
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


            exception.observe(this@MainTimeLineFragment , Observer {
                it?.let {
                    adapter.emptyView = View.inflate(activity, R.layout.item_no_content, null)
                }
            })
            dataRefreshed.observe(this@MainTimeLineFragment, Observer {
                refresh.setRefreshing(false)
            })
            dataInserted.observe(this@MainTimeLineFragment, Observer {
                val positionIndex = (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                if (positionIndex == 0) {
                    recycler.layoutManager.scrollToPosition(0)
                }
            })

        }

    }

    override fun onResume() {
        super.onResume()
        viewmodel.isBackground = false
    }

    override fun onStop() {
        super.onStop()
        viewmodel.isBackground = true
    }

}