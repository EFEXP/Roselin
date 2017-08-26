package xyz.donot.roselin.view.fragment

import android.os.Bundle
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.content_base_fragment.view.*
import twitter4j.Query
import twitter4j.Status
import xyz.donot.roselin.util.getDeserialized
import xyz.donot.roselin.view.fragment.status.TimeLineFragment

class SearchTimeline : TimeLineFragment() {
    private  var query :Query?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        query=arguments.getByteArray("query_bundle").getDeserialized<Query>()

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view?.refresh?.isEnabled=false
    }

    override fun GetData(): MutableList<Status>? {
        if (query==null){return null}

        val result=twitter.search(query)
        if(result!=null){
            query = if (result.hasNext()) {
                result.nextQuery()
            } else{
                adapter.loadMoreEnd()
                null
            }
            return result.tweets
        }

        return null
    }

    override fun pullToRefresh(adapter: BaseQuickAdapter<Status, BaseViewHolder>) {

    }
}


