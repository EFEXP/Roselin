package xyz.donot.roselin.view.fragment

import android.os.Bundle
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.Query
import twitter4j.Status
import xyz.donot.roselin.util.extraUtils.async
import xyz.donot.roselin.util.extraUtils.mainThread
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.util.getDeserialized
import xyz.donot.roselin.view.fragment.status.TimeLineFragment

class SearchTweet : TimeLineFragment() {
    private  var query :Query?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        query=arguments.getByteArray("query_bundle").getDeserialized<Query>()
    }

    override fun GetData(): MutableList<Status>? {
        val result=twitter.search(query)
        if(result!=null){
            if (result.hasNext())
            {
                query=result.nextQuery()
            }
            else{
                adapter.loadMoreEnd()
            }
            return result.tweets
        }
        return null

    }

    override fun pullToRefresh(adapter: BaseQuickAdapter<Status, BaseViewHolder>) {
        async {
            val since=adapter.data[0].id
            val q=  query!!.apply { sinceId=since }
            try {
                val result =twitter.search(q)
                if (result!=null){
                    mainThread {
                        adapter.addData(0,result.tweets)
                        recycler.smoothScrollToPosition(0) }
                }
            }
            catch (e:Exception){ toast(e.localizedMessage)}
        }
    }
}


