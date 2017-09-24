package xyz.donot.roselinx.view.fragment.status

import android.app.Application
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Paging
import twitter4j.Status
import twitter4j.TwitterException
import xyz.donot.roselinx.R
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.util.extraUtils.delayed
import xyz.donot.roselinx.util.extraUtils.toast
import xyz.donot.roselinx.util.extraUtils.twitterExceptionMessage
import xyz.donot.roselinx.util.getDeserialized
import xyz.donot.roselinx.view.custom.MyLoadingView
import xyz.donot.roselinx.view.fragment.base.MainTimeLineFragment
import xyz.donot.roselinx.view.fragment.base.MainTimeLineViewModel

class MentionTimeLine : MainTimeLineFragment() {
   override val viewmodel: MentionViewModel by lazy { ViewModelProviders.of(this).get(MentionViewModel::class.java) }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.apply {
            twitter = arguments.getByteArray("twitter").getDeserialized()
                adapter.apply {
                    setOnLoadMoreListener({ viewmodel.loadMoreData() }, recycler)
                    setLoadMoreView(MyLoadingView())
                    emptyView = View.inflate(activity, R.layout.item_empty, null)
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
                loadMoreData()}
            
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

class MentionViewModel(application: Application) : MainTimeLineViewModel(application) {
    private val replyReceiver by lazy { ReplyReceiver() }


    fun initService() {
        LocalBroadcastManager.getInstance(getApplication()).apply {
            registerReceiver(replyReceiver, IntentFilter("NewReply"))
        }
    }

    fun pullDown() {
        if (adapter.data.isNotEmpty()) {
            launch(UI) {
                async(CommonPool) { twitter.getMentionsTimeline(Paging(adapter.data[0].id)) }.await()?.let { insertDataBackground(it) }
                dataRefreshed.call()
            }
        } else {
            dataRefreshed.call()
        }
    }

    fun loadMoreData() {
        launch(UI) {
            try {
                val result = async(CommonPool) { twitter.getMentionsTimeline(Paging(page)) }.await()
                if (result == null || result.isEmpty()) {
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

    inner class ReplyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val data = intent.extras.getByteArray("Status").getDeserialized<Status>()
            insertDataBackground(data)
        }
    }

    override fun onCleared() {
        super.onCleared()
        LocalBroadcastManager.getInstance(getApplication()).apply {
            unregisterReceiver(replyReceiver)
        }

    }
}