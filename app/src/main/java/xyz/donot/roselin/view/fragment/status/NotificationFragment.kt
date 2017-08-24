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
import twitter4j.Status
import xyz.donot.roselin.util.extraUtils.mainThread
import xyz.donot.roselin.util.getDeserialized

class NotificationFragment:TimeLineFragment(){
    private val rtReceiver by lazy { RTReceiver() }
    private val favReceiver by lazy { FavReceiver() }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LocalBroadcastManager.getInstance(activity).apply {
            registerReceiver(rtReceiver, IntentFilter("onRetweeted"))
            registerReceiver(favReceiver, IntentFilter("OnFavorited"))
        }
    }
    override fun loadMore(adapter: BaseQuickAdapter<Status, BaseViewHolder>) {

    }

    override fun pullToRefresh(adapter: BaseQuickAdapter<Status, BaseViewHolder>) {

    }
    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(activity).apply {
            unregisterReceiver(favReceiver)
            unregisterReceiver(rtReceiver)
        }
    }

    inner class RTReceiver : BroadcastReceiver() {
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


    inner class FavReceiver : BroadcastReceiver() {
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
