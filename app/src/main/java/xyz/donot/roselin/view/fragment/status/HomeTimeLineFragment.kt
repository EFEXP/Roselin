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
import twitter4j.StatusDeletionNotice
import xyz.donot.roselin.util.extraUtils.async
import xyz.donot.roselin.util.extraUtils.mainThread
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.util.getDeserialized

class HomeTimeLineFragment : TimeLineFragment(){
    override fun GetData(): ResponseList<Status>? =twitter.getHomeTimeline(Paging(page))
    private val receiver by lazy { StatusReceiver() }
    private val deleteReceiver by lazy { DeleteReceiver() }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LocalBroadcastManager.getInstance(activity).apply {
            registerReceiver(receiver, IntentFilter("NewStatus"))
            registerReceiver(deleteReceiver, IntentFilter("DeleteStatus"))
        }
    }

    override fun pullToRefresh(adapter: BaseQuickAdapter<Status, BaseViewHolder>) {
        async {
            try {
            val result =twitter.getHomeTimeline(Paging(adapter.data[0].id))
            if (result.isNotEmpty()){
             mainThread {
                insertDataBackground(result)
                 recycler.smoothScrollToPosition(0) }
             }
            }
            catch (e:Exception){ toast(e.localizedMessage)}
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(activity).apply {
            unregisterReceiver(receiver)
            unregisterReceiver(deleteReceiver)
        }
    }
    //Receiver
    inner class StatusReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
          val  positionIndex =  (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val data=intent.extras.getByteArray("Status").getDeserialized<Status>()
            mainThread {
                insertDataBackground(data)
                if (positionIndex==0) {
                    (recycler).smoothScrollToPosition(0)
                }

            }
        }
    }
    inner class DeleteReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val data=intent.extras.getByteArray("StatusDeletionNotice").getDeserialized<StatusDeletionNotice>()
            mainThread {
                adapter.data.filter { de -> de.id == data.statusId }.mapNotNull {
                    val int=   adapter.data.indexOf(it)
                    adapter.remove(int)
                }

            }
        }
    }
}


