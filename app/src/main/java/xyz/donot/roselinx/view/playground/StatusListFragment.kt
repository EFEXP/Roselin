package xyz.donot.roselinx.view.playground

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.Status
import twitter4j.TwitterException
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.mainThread
import xyz.donot.roselinx.view.adapter.StatusAdapter
import xyz.donot.roselinx.view.custom.SingleLiveEvent
import xyz.donot.roselinx.view.fragment.ARecyclerFragment

abstract class MainTimeLineViewModel(app: Application) : AndroidViewModel(app) {
    val adapter by lazy { StatusAdapter() }
    val dataInserted = SingleLiveEvent<Unit>()
    val dataRefreshed = SingleLiveEvent<Unit>()
    private val dataStore: ArrayList<Status> = ArrayList()
    val exception = MutableLiveData<TwitterException>()
    protected var page: Int = 0
        get() {
            field++
            return field
        }

    fun insertDataBackground(data: List<Status>) = mainThread {
        mainThread {
            if (isBackground) {
                dataStore.addAll(0, data)
            } else {
                adapter.addData(0, data)
                dataInserted.call()
            }
        }
    }

    fun endAdapter() = mainThread {
        adapter.loadMoreEnd(true)
    }
    var isBackground = false
        set(value) {
            if (!value)
                if (dataStore.isNotEmpty()) {
                    adapter.addData(0, dataStore)
                    dataStore.clear()
                    dataInserted.call()
                }
        }

    fun insertDataBackground(data: Status) = mainThread {
        mainThread {
            if (isBackground) {
                dataStore.add(0, data)
            } else {
                adapter.addData(0, data)
                dataInserted.call()
            }
        }
    }

}

abstract class MainTimeLineFragment: ARecyclerFragment(){
    abstract val viewmodel:MainTimeLineViewModel
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.apply {
            exception.observe(this@MainTimeLineFragment , Observer {
                it?.let {
                    adapter.emptyView = View.inflate(activity, R.layout.item_no_content, null)
                }
            })
            dataRefreshed.observe(this@MainTimeLineFragment, Observer {
                refresh.setRefreshing(false)
            })
            dataInserted.observe(this@MainTimeLineFragment, Observer {
                val positionIndex = (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                if (positionIndex == 0) {
                    recycler.layoutManager.scrollToPosition(0)
                }
            })

        }

    }

    override fun onResume() {
        super.onResume()
        viewmodel.isBackground = false
    }

    override fun onStop() {
        super.onStop()
        viewmodel.isBackground = true
    }

}