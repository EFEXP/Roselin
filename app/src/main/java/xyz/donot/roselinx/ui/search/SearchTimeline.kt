package xyz.donot.roselinx.ui.search

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.Query
import twitter4j.Status
import xyz.donot.roselinx.R
import xyz.donot.roselinx.ui.base.ARecyclerFragment
import xyz.donot.roselinx.ui.dialog.getTweetDialog
import xyz.donot.roselinx.ui.util.extraUtils.delayed
import xyz.donot.roselinx.ui.util.getDeserialized
import xyz.donot.roselinx.ui.view.MyLoadingView

class SearchTimeline : ARecyclerFragment() {
    val viewmodel: SearchViewModel by lazy { ViewModelProviders.of(this).get(SearchViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel.query.value = arguments!!.getByteArray("query_bundle").getDeserialized<Query>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.apply {
            adapter.apply {
                setOnLoadMoreListener({ viewmodel.loadMoreData() }, recycler)
                setLoadMoreView(MyLoadingView())
                emptyView = View.inflate(activity, R.layout.item_empty, null)
                setOnItemClickListener { adapter, _, position ->
                    val status = adapter.data[position] as Status
                    getTweetDialog(activity!!, this@SearchTimeline, viewmodel.mainTwitter, status)?.show()

                }

            }
            recycler.adapter = adapter
            refresh.isEnabled = true

            if (savedInstanceState == null)
                viewmodel.loadMoreData()
            viewmodel.exception.observe(this@SearchTimeline, Observer {
                it?.let {
                    adapter.emptyView = View.inflate(activity, R.layout.item_no_content, null)
                }
            })
            dataRefreshed.observe(this@SearchTimeline, Observer {
                refresh.setRefreshing(false)
            })

            /*  adapter.addHeaderView(
                      View.inflate(activity, R.layout.item_ad, null).apply {
                          adView.loadAd(AdRequest.Builder()
                                  .setGender(AdRequest.GENDER_MALE)
                                  .addTestDevice("0CF83648F3E630518CF53907939C9A8D")
                                  .addTestDevice("6D38172C5A30A07095F6420BC145C497")
                                  .build())
                      }
              )*/

            refresh.setOnRefreshListener {
                Handler().delayed(1000, {
                    pullDown()
                })
            }
        }
    }

}


