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
import kotlinx.coroutines.experimental.async
import twitter4j.Paging
import twitter4j.ResponseList
import twitter4j.Status
import xyz.donot.roselinx.util.extraUtils.mainThread
import xyz.donot.roselinx.util.getDeserialized

class MentionTimeLine :TimeLineFragment(){
    override fun GetData(): ResponseList<Status>? =viewmodel.twitter.getMentionsTimeline(Paging(page))
    private val replyReceiver by lazy { ReplyReceiver () }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState==null){
            LocalBroadcastManager.getInstance(activity).apply {
                registerReceiver(replyReceiver, IntentFilter("NewReply"))
            }
        }
        viewmodel.pullToRefresh= {twitter->
            async(CommonPool){twitter.getMentionsTimeline(Paging(viewmodel.adapter.data[0].id))}
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(activity).apply {
            unregisterReceiver(replyReceiver)
        }
    }

    inner class ReplyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val  positionIndex =  (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val data=intent.extras.getByteArray("Status").getDeserialized<Status>()
            mainThread {
                viewmodel .adapter.addData(0,data)
                if (positionIndex==0) {
                    (recycler).smoothScrollToPosition(0)
                }

            }
        }
    }
}

