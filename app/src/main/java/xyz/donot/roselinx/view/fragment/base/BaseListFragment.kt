@file:Suppress("UNCHECKED_CAST")

package xyz.donot.roselinx.view.fragment.base

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.android.synthetic.main.item_ad.view.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.delayed
import xyz.donot.roselinx.util.getAccount
import xyz.donot.roselinx.util.getDeserialized
import xyz.donot.roselinx.view.custom.MyLoadingView
import xyz.donot.roselinx.viewmodel.fragment.BaseListViewModel

abstract class BaseListFragment<T> : ARecyclerFragment() {
    protected val viewmodel: BaseListViewModel<T> by lazy { ViewModelProviders.of(this).get(BaseListViewModel::class.java) as BaseListViewModel<T> }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.apply {
            twitter = if (arguments != null && arguments.containsKey("twitter")) {
                arguments.getByteArray("twitter").getDeserialized()
            } else getAccount().account
            adapter!!.apply {
                setOnLoadMoreListener({ viewmodel.loadMoreData() }, recycler)
                setLoadMoreView(MyLoadingView())
                emptyView = View.inflate(activity, R.layout.item_empty, null)
                if (savedInstanceState == null)
                addHeaderView(View.inflate(activity, R.layout.item_ad, null).apply {
                            adView.loadAd(AdRequest.Builder()
                                    .setGender(AdRequest.GENDER_MALE)
                                    .addTestDevice("0CF83648F3E630518CF53907939C9A8D")
                                    .addTestDevice("6D38172C5A30A07095F6420BC145C497")
                                    .build())
                        })
            }
            recycler.adapter = adapter
            if (savedInstanceState == null)
                viewmodel.loadMoreData()

            viewmodel.exception.observe(this@BaseListFragment, Observer {
                it?.let {
                    adapter!!.emptyView = View.inflate(activity, R.layout.item_no_content, null)
                }
            })
            dataRefreshed.observe(this@BaseListFragment, Observer {
                refresh.setRefreshing(false)
            })
            dataInserted.observe(this@BaseListFragment, Observer {
                val positionIndex = (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                if (positionIndex == 0) {
                    recycler.layoutManager.scrollToPosition(0)
                }
            })
            refresh.setOnRefreshListener {
                Handler().delayed(1000, {
                    pullDown()
                })
            }
            isBackground.observe(this@BaseListFragment, Observer {
                it?.let { isBack ->
                    if (dataStore.isNotEmpty() && isBack.not()) {
                        adapter!!.addData(0, dataStore)
                        val positionIndex = (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                        if (positionIndex == 0) recycler.layoutManager.scrollToPosition(0)
                        dataStore.clear()
                    }
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        viewmodel.isBackground.value = false
    }

    override fun onStop() {
        super.onStop()
        viewmodel.isBackground.value = true
    }


}

