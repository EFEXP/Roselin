package xyz.donot.roselinx.view.fragment.status

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import twitter4j.Paging
import twitter4j.ResponseList
import twitter4j.Status
import twitter4j.StatusDeletionNotice
import xyz.donot.roselinx.util.extraUtils.mainThread
import xyz.donot.roselinx.util.getDeserialized


class HomeTimeLineFragment : TimeLineFragment(){
    override fun GetData(): ResponseList<Status>? =viewmodel.twitter.getHomeTimeline(Paging(page))
    private val receiver by lazy { StatusReceiver() }
    private val deleteReceiver by lazy { DeleteReceiver() }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState==null&&viewmodel.twitter==viewmodel.main_twitter){
            LocalBroadcastManager.getInstance(activity).apply {
                registerReceiver(receiver, IntentFilter("NewStatus"))
                registerReceiver(deleteReceiver, IntentFilter("DeleteStatus"))
            }
        }
        viewmodel.pullToRefresh= {twitter->
            async(CommonPool){ twitter.getHomeTimeline(Paging(viewmodel.adapter.data[0].id))}
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
            val data=intent.extras.getByteArray("Status").getDeserialized<Status>()
            mainThread {
                viewmodel.insertDataBackground(data)
            }
        }
    }
    inner class DeleteReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val data=intent.extras.getByteArray("StatusDeletionNotice").getDeserialized<StatusDeletionNotice>()
            mainThread {
                viewmodel . adapter.data.filter { de -> de.id == data.statusId }.mapNotNull {
                    val int=   viewmodel .adapter.data.indexOf(it)
                    viewmodel .  adapter.remove(int)
                }

            }
        }
    }
}


