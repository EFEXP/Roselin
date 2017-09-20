package xyz.donot.roselinx.view.fragment.status

import android.os.Bundle
import android.view.View
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Query
import twitter4j.TwitterException
import xyz.donot.roselinx.util.extraUtils.toast
import xyz.donot.roselinx.util.extraUtils.twitterExceptionMessage
import xyz.donot.roselinx.util.getDeserialized

class SearchTimeline : TimeLineFragment() {
    private var query: Query? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        query = arguments.getByteArray("query_bundle").getDeserialized<Query>()
    }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        viewmodel.useDefaultLoad = false
        super.onViewCreated(view, savedInstanceState)
        viewmodel.pullToRefresh = { _ ->
            viewmodel.adapter.data.clear()
            viewmodel.adapter.notifyDataSetChanged()
            query = arguments.getByteArray("query_bundle").getDeserialized<Query>()
            loadMoreData2()
            viewmodel.dataRefreshed.call()
            null
        }
    }

    var load=true
    override fun loadMoreData2() {
        if (load)
        launch(UI) {
            try {
                val result = async(CommonPool) { viewmodel.twitter.search(query) }.await()
                if (result.hasNext()) {
                    query = result.nextQuery()
                    viewmodel.adapter.loadMoreComplete()
                } else {
                    load=false
                    viewmodel.endAdapter()
                }
                viewmodel.adapter.addData(result.tweets)
            } catch (e: TwitterException) {
               activity.toast(twitterExceptionMessage(e))

            }
        }
    }
}


