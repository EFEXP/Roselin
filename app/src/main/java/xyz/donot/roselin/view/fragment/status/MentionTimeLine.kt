package xyz.donot.roselin.view.fragment.status

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.Paging
import twitter4j.ResponseList
import twitter4j.Status
import xyz.donot.roselin.util.extraUtils.async
import xyz.donot.roselin.util.extraUtils.mainThread
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.util.getDeserialized

class MentionTimeLine :TimeLineFragment(){
    override fun GetData(): ResponseList<Status>? =twitter.getMentionsTimeline(Paging(page))
    private val replyReceiver by lazy { ReplyReceiver () }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LocalBroadcastManager.getInstance(activity).apply {
            registerReceiver(replyReceiver, IntentFilter("NewReply"))
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(activity).apply {
            unregisterReceiver(replyReceiver)
        }
    }
    override fun pullToRefresh(adapter: BaseQuickAdapter<Status, BaseViewHolder>) {
        async {
            try {
                val result= twitter.getMentionsTimeline(Paging(adapter.data[0].id))
                if (result.isNotEmpty()){
                    mainThread {
                        insertDataBackground(result)
                        recycler.smoothScrollToPosition(0) }
                }
            }
            catch (e:Exception){ toast(e.localizedMessage)}
        }
      }
    inner class ReplyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val  positionIndex =  (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val data=intent.extras.getByteArray("Status").getDeserialized<Status>()
            mainThread {
                adapter.addData(0,data)
                if (positionIndex==0) {
                    (recycler).smoothScrollToPosition(0)
                }

            }
        }
    }
}

