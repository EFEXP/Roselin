package xyz.donot.roselin.view.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.Paging
import twitter4j.ResponseList
import twitter4j.Status
import twitter4j.Twitter
import xyz.donot.roselin.extend.SafeAsyncTask
import xyz.donot.roselin.util.extraUtils.mainThread
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.util.getDeserialized

class HomeTimeLineFragment :TimeLineFragment(){
    private val receiver by lazy { StatusReceiver() }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LocalBroadcastManager.getInstance(activity).registerReceiver(receiver, IntentFilter("NewStatus"))
        Log.d("DataReceiver", " setUpBroadCast()")

    }
    override fun loadMore(adapter: BaseQuickAdapter<Status, BaseViewHolder>) {
        class HomeTimeLineTask: SafeAsyncTask<Twitter,ResponseList<Status>>() {
            override fun doTask(arg: Twitter): ResponseList<twitter4j.Status> =
                    arg.getHomeTimeline(Paging(page))
            override fun onSuccess(result: ResponseList<twitter4j.Status>) {
                adapter.addData(result)
                adapter.loadMoreComplete()
            }

            override fun onFailure(exception: Exception) {
                toast(exception.localizedMessage)
            }

        }
        HomeTimeLineTask().execute(twitter)
    }
    override fun pullToRefresh(adapter: BaseQuickAdapter<Status, BaseViewHolder>) {
        val asyncTask: SafeAsyncTask<Twitter, ResponseList<Status>> = object : SafeAsyncTask<Twitter, ResponseList<Status>>() {
            override fun doTask(arg: Twitter): ResponseList<twitter4j.Status> =
                    arg.getHomeTimeline(Paging(adapter.data[0].id))
            override fun onSuccess(result: ResponseList<twitter4j.Status>) {
                if (result.isNotEmpty()){
                    adapter.addData(0,result)
                    recycler.smoothScrollToPosition(0)
                }

            }

            override fun onFailure(exception: Exception) {
                toast(exception.localizedMessage)
            }

        }
        asyncTask.execute(twitter)
    }
    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiver)
    }
    inner class StatusReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("DataReceiver", "onReceive")
          val  positionIndex =  (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val data=intent.extras.getByteArray("Status").getDeserialized<Status>()
            mainThread {
                if (positionIndex==0) {
                    adapter.addData(0,data)
                    (recycler).smoothScrollToPosition(0)
                }

            }
        }
    }

}


