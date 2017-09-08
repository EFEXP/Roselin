package xyz.donot.roselinx.view.fragment.status

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Query
import twitter4j.Status
import xyz.donot.roselinx.util.extraUtils.mainThread
import xyz.donot.roselinx.util.extraUtils.twitterExceptionToast
import xyz.donot.roselinx.util.getDeserialized

class SearchTimeline : TimeLineFragment() {
    private var query: Query? = null
    private val receiver by lazy { SearchReceiver() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        query = arguments.getByteArray("query_bundle").getDeserialized<Query>()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        viewmodel.useDefaultLoad = false
        super.onViewCreated(view, savedInstanceState)
        if (arguments.getString("query_text") != null && savedInstanceState == null) {
            LocalBroadcastManager.getInstance(activity).apply {
                registerReceiver(receiver, IntentFilter(arguments.getString("query_text")))
            }
        }
        viewmodel.pullToRefresh= {twitter->
            viewmodel. adapter.data.clear()
            viewmodel. adapter.notifyDataSetChanged()
            query = arguments.getByteArray("query_bundle").getDeserialized<Query>()
            LoadMoreData2()
            viewmodel. dataRefreshed
            null

        }
    }


    override fun GetData(): MutableList<Status>? = null
    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(activity).apply {
            unregisterReceiver(receiver)
        }
    }

    override fun LoadMoreData2() {
        launch(UI) {
            try {
                val result = async(CommonPool) { viewmodel.twitter.search(query) }.await()
                if (result.hasNext()) {
                    query = result.nextQuery()
                    viewmodel.adapter.loadMoreComplete()
                } else {
                    query = null
                    viewmodel.shouldLoad = false
                }
                viewmodel.adapter.addData(result.tweets)
            } catch (e: Exception) {
                activity.twitterExceptionToast(e)

            }
        }
    }
    //Receiver
    inner class SearchReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val positionIndex = (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val data = intent.extras.getByteArray("Status").getDeserialized<Status>()
            mainThread {
                viewmodel.insertDataBackground(data)
                if (positionIndex == 0) {
                    (recycler).smoothScrollToPosition(0)
                }
            }
        }
    }
}


