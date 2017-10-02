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
import twitter4j.StatusDeletionNotice
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


class HomeTimeLineFragment : MainTimeLineFragment() {
    override val viewmodel: HomeTimeLineViewModel by lazy { ViewModelProviders.of(this).get(HomeTimeLineViewModel::class.java) }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.apply {
            twitter = arguments.getByteArray("twitter").getDeserialized()
            adapter.apply {
                setOnLoadMoreListener({ viewmodel.loadMoreData() }, recycler)
                setLoadMoreView(MyLoadingView())
                emptyView = View.inflate(activity, R.layout.item_empty, null)
            }
            if (savedInstanceState == null) {
                loadMoreData()
                initService()
            }
            recycler.adapter = adapter
            recycler.isNestedScrollingEnabled = false

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

