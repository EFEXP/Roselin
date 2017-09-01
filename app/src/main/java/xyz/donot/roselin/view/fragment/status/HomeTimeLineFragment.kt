package xyz.donot.roselin.view.fragment.status

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import twitter4j.Paging
import twitter4j.ResponseList
import twitter4j.Status
import twitter4j.StatusDeletionNotice
import xyz.donot.roselin.util.extraUtils.mainThread
import xyz.donot.roselin.util.extraUtils.tExceptionToast
import xyz.donot.roselin.util.getDeserialized
import xyz.donot.roselin.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselin.view.custom.MyViewHolder

class HomeTimeLineFragment : TimeLineFragment(){
    override fun GetData(): ResponseList<Status>? =twitter.getHomeTimeline(Paging(page))
    private val receiver by lazy { StatusReceiver() }
    private val deleteReceiver by lazy { DeleteReceiver() }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState==null&&twitter==main_twitter){
            LocalBroadcastManager.getInstance(activity).apply {
                registerReceiver(receiver, IntentFilter("NewStatus"))
                registerReceiver(deleteReceiver, IntentFilter("DeleteStatus"))
            }
        }
    }

    override fun pullToRefresh(adapter: MyBaseRecyclerAdapter<Status, MyViewHolder>) {
        launch(UI){
            try {
                val result =twitter.getHomeTimeline(Paging(adapter.data[0].id))
                insertDataBackground(result)
            } catch (e: Exception) {
               activity.tExceptionToast(e)
            }
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
                insertDataBackground(data)
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


