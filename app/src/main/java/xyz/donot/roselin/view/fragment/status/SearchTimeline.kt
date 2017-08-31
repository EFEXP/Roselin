package xyz.donot.roselin.view.fragment.status

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.Query
import twitter4j.Status
import xyz.donot.roselin.util.extraUtils.asyncDeprecated
import xyz.donot.roselin.util.extraUtils.mainThread
import xyz.donot.roselin.util.getDeserialized
import xyz.donot.roselin.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselin.view.custom.MyViewHolder

class SearchTimeline : TimeLineFragment() {
    private  var query :Query?=null
    private val receiver by lazy { SearchReceiver() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        query=arguments.getByteArray("query_bundle").getDeserialized<Query>()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        useDefaultLoad=false
        super.onViewCreated(view, savedInstanceState)
       if (arguments.getString("query_text")!=null&& savedInstanceState==null){
        LocalBroadcastManager.getInstance(activity).apply {
            registerReceiver(receiver, IntentFilter(arguments.getString("query_text")))
        }}
    }

    override fun GetData(): MutableList<Status>? =null
    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(activity).apply {
            unregisterReceiver(receiver) } }

    override fun LoadMoreData2() {
        asyncDeprecated {
      try {
            val result=twitter.search(query)
            if (result!=null){
            mainThread {
                if (result.hasNext()) {
                    query = result.nextQuery()
                    adapter.loadMoreComplete()
                } else {
                    query = null
                    shouldLoad=false
                }
                adapter.addData(result.tweets)
            }
            }
        }catch (e:Exception){
          e.printStackTrace()
      }
        }
    }

    override fun getInitialData2() {
        asyncDeprecated {
            try {
                val result=twitter.search(query)
                if (result!=null){
                    mainThread {
                        if (result.hasNext()) {
                            query = result.nextQuery()
                            adapter.loadMoreComplete()
                        } else {
                            query = null
                            shouldLoad=false
                        }
                        adapter.addData(result.tweets)
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    override fun pullToRefresh(adapter: MyBaseRecyclerAdapter<Status, MyViewHolder>) {
        adapter.data.clear()
        adapter.notifyDataSetChanged()
        query=arguments.getByteArray("query_bundle").getDeserialized<Query>()
        LoadMoreData2()
    }
    //Receiver
    inner class SearchReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val  positionIndex =  (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val data=intent.extras.getByteArray("Status").getDeserialized<Status>()
            mainThread {
                insertDataBackground(data)
                if (positionIndex==0) {
                    (recycler).smoothScrollToPosition(0)
                }

            }
        }}}


