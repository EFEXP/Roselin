@file:Suppress("UNCHECKED_CAST")

package xyz.donot.roselinx.view.fragment

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.android.synthetic.main.item_ad.view.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.delayed
import xyz.donot.roselinx.util.getDeserialized
import xyz.donot.roselinx.util.getTwitterInstance
import xyz.donot.roselinx.view.custom.MyLoadingView
import xyz.donot.roselinx.viewmodel.fragment.BaseListViewModel
abstract class BaseListFragment<T> : ARecyclerFragment() {
    protected val viewmodel: BaseListViewModel<T> by lazy { ViewModelProviders.of(this).get(BaseListViewModel::class.java) as BaseListViewModel<T> }
    abstract val adapterx: BaseQuickAdapter<T, BaseViewHolder>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.content_base_fragment, container, false)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.apply {
            adapter = adapterx
            twitter = if (arguments != null && arguments.containsKey("twitter")) {
                arguments.getByteArray("twitter").getDeserialized()
            } else getTwitterInstance()
            adapter.apply {
                setOnLoadMoreListener({
                      if (viewmodel.useDefaultLoad) {
                          viewmodel.loadMoreData()
                      } else {
                          loadMoreData2()
                      }
                }
                        , recycler)
                setLoadMoreView(MyLoadingView())
                emptyView = View.inflate(activity, R.layout.item_empty, null)
            }
            recycler.adapter = adapter

            if (savedInstanceState == null)
                if (useDefaultLoad) {
                    viewmodel.loadMoreData()
                } else {
                    loadMoreData2()
                }
            else {
                val t = savedInstanceState.getSerializable("data") as ArrayList<T>
                Log.d("SavedInstanceStateHas", t.size.toString())
                adapter.addData(t)
            }


            viewmodel.exception.observe(this@BaseListFragment, Observer {
                it?.let {
                    adapter.emptyView = View.inflate(activity, R.layout.item_no_content, null)
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
                Handler().delayed(1000,{
                pullDown()})
            }

            adapter.addHeaderView(
                    View.inflate(activity, R.layout.item_ad, null).apply {
                        adView.loadAd(AdRequest.Builder()
                                .setGender(AdRequest.GENDER_MALE)
                                .addTestDevice("0CF83648F3E630518CF53907939C9A8D")
                                .addTestDevice("6D38172C5A30A07095F6420BC145C497")
                                .build())
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
    }

    override fun onResume() {
        super.onResume()
        viewmodel.isBackground.value = false
    }

    override fun onStop() {
        super.onStop()
        viewmodel.isBackground.value = true
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.content_base_fragment)
        return dialog
    }

    open fun loadMoreData2() = Unit

    fun reselect() = recycler.smoothScrollToPosition(0)

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        val l = ArrayList<T>()
        l.addAll(viewmodel.adapter.data)
        Log.d("GiveData", viewmodel.adapter.data.count().toString())
        outState?.putSerializable("data", l)
    }

}

