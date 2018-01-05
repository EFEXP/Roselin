package xyz.donot.roselinx.ui.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.model.entity.HOME_TIMELINE
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.ui.base.ARecyclerFragment
import xyz.donot.roselinx.ui.dialog.getTweetDialog
import xyz.donot.roselinx.ui.status.TweetAdapter
import xyz.donot.roselinx.ui.util.extraUtils.delayed
import xyz.donot.roselinx.ui.util.getDeserialized

class HomeTimeLineFragment : ARecyclerFragment() {
    val viewmodel: HomeTimelineViewModel by lazy { ViewModelProviders.of(this).get(HomeTimelineViewModel::class.java) }
    val adapter by lazy { TweetAdapter(activity!!) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.apply {
            twitter = arguments!!.getByteArray("twitter").getDeserialized()
            adapter.apply {
                onLoadMore = { viewmodel.loadMoreData(true) }
                adapter.onItemClick = { (status), _ ->
                        getTweetDialog(activity!!, this@HomeTimeLineFragment, viewmodel.mainTwitter, status)?.show()
                }
                dataRefreshed.observe(this@HomeTimeLineFragment, Observer {
                    refresh.setRefreshing(false)
                })
            }
            if (savedInstanceState == null) {
                initService()
            }
            launch(UI) {
                async {
                    RoselinDatabase.getInstance().tweetDao().getAllDataSource(HOME_TIMELINE,twitter.id)
                            .create(0, PagedList.Config.Builder().setPageSize(50).setPrefetchDistance(50).build()) }.await()
                        .observe(this@HomeTimeLineFragment, Observer {
                            it?.let {
                                adapter.setList(it)
                            }
                        })
            }
            viewmodel.loadMoreData(false)
            recycler.adapter = adapter
            refresh.setOnRefreshListener {
                Handler().delayed(1000, {
                    pullDown()
                })
            }

        }
        refresh.isEnabled = true
    }
}


