package xyz.donot.roselinx.ui.base

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import twitter4j.Twitter
import twitter4j.TwitterException
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.ui.util.extraUtils.mainThread
import xyz.donot.roselinx.ui.util.extraUtils.toast
import xyz.donot.roselinx.ui.util.extraUtils.twitterExceptionMessage
import xyz.donot.roselinx.ui.util.getAccount
import xyz.donot.roselinx.ui.view.SingleLiveEvent
import kotlin.properties.Delegates


open class BaseListViewModel<T>(app: Application) : AndroidViewModel(app) {
    var isBackground = MutableLiveData<Boolean>()
    var twitter by Delegates.notNull<Twitter>()
    val exception = MutableLiveData<TwitterException>()
    val mainTwitter by lazy { getAccount() }
    val dataInserted = SingleLiveEvent<Unit>()
    val dataRefreshed = SingleLiveEvent<Unit>()
    val dataStore: ArrayList<T> = ArrayList()
    var adapter:BaseQuickAdapter<T, BaseViewHolder>?=null
    set(value) {
        if (field==null)
        field=value
    }
    lateinit var getData: (Twitter) -> Deferred<List<T>?>
    var page: Int = 0
        get() {
            field++
            return field
        }

    private fun insertDataBackground(data: List<T>) = mainThread {
        if (isBackground.value!!) {
            dataStore.addAll(0, data)
        } else {
            adapter!!.addData(0, data)
            dataInserted.call()
        }
    }


    lateinit var pullToRefresh: (Twitter) -> Deferred<List<T>>?

    fun pullDown() {
        if (adapter!!.data.isNotEmpty()) {
            launch(UI) {
                pullToRefresh(twitter)?.await()?.let { insertDataBackground(it) }
                dataRefreshed.call()
            }
        } else {
            dataRefreshed.call()
        }
    }

    fun endAdapter() = mainThread {
        adapter?.loadMoreEnd(true)
    }

    fun loadMoreData() {
        launch(UI) {
            try {
                val result = getData(twitter).await()
                if (result == null || result.isEmpty()) {
                    endAdapter()
                } else {
                    adapter?.addData(result)
                    adapter?.loadMoreComplete()
                }
            } catch (e: TwitterException) {
                adapter?.loadMoreFail()
                exception.value = e
                getApplication<Roselin>().toast(twitterExceptionMessage(e))
            }
        }
    }
}


