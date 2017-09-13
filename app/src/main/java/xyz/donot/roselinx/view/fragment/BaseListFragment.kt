@file:Suppress("UNCHECKED_CAST")

package xyz.donot.roselinx.view.fragment

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.android.synthetic.main.item_ad.view.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.getDeserialized
import xyz.donot.roselinx.util.getTwitterInstance
import xyz.donot.roselinx.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselinx.view.custom.MyLoadingView
import xyz.donot.roselinx.view.custom.MyViewHolder
import xyz.donot.roselinx.viewmodel.fragment.BaseListViewModel
import kotlin.properties.Delegates

//PullToRefresh  付き
abstract class BaseListFragment<T> : ARecyclerFragment(), LifecycleRegistryOwner {
    protected var viewmodel by Delegates.notNull<BaseListViewModel<T>>()
    abstract fun adapterFun(): MyBaseRecyclerAdapter<T, MyViewHolder>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewmodel = ViewModelProviders.of(this).get(BaseListViewModel::class.java) as BaseListViewModel<T>
        viewmodel.twitter = if (arguments != null && arguments.containsKey("twitter")) {
            arguments.getByteArray("twitter").getDeserialized()
        } else getTwitterInstance()
        return inflater.inflate(R.layout.content_base_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.apply {
            initAdapter(adapterFun())
            adapter.apply {
                setOnLoadMoreListener({
                    if (viewmodel.shouldLoad) {
                        if (viewmodel.useDefaultLoad) {
                            viewmodel.LoadMoreData()
                        } else {
                            LoadMoreData2()
                        }
                    }
                }, recycler)
                setLoadMoreView(MyLoadingView())
                emptyView = View.inflate(activity, R.layout.item_empty, null)
            }
            recycler.adapter = adapter
            if (savedInstanceState == null) {
                if (useDefaultLoad) {
                    viewmodel.LoadMoreData()
                } else {
                    LoadMoreData2()
                }
            }
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
                pullDown()
            }

            adapter.addHeaderView(
                    View.inflate(activity, R.layout.item_ad, null).apply {
                        adView.loadAd(AdRequest.Builder().setGender(AdRequest.GENDER_MALE).build())
                    }
            )
            isBackground.observe(this@BaseListFragment, Observer {
                it?.let { isBack ->
                    if (dataStore.isNotEmpty() && isBack.not()) {
                        adapter.addData(0, dataStore)
                        val positionIndex = (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                        if (positionIndex == 0) recycler.layoutManager.scrollToPosition(0)
                        dataStore.clear()
                    }
                }
            })
        }
        refresh.isEnabled = false
        viewmodel.endLoad.observe(this, Observer {
            it?.let {
              //  if (it)refresh.setRefreshing(false)
            }
        })
    }
    override fun onResume() {
        super.onResume()
        viewmodel.isBackground.value = false
    }

    override fun onStop() {
        super.onStop()
        viewmodel.isBackground.value = true
    }

    open fun LoadMoreData2() = Unit



    private val life by lazy { LifecycleRegistry(this) }
    override fun getLifecycle(): LifecycleRegistry {
        return life
    }


}

