package xyz.donot.roselinx.ui.search

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Query
import twitter4j.Status
import twitter4j.TwitterException
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.ui.status.StatusAdapter
import xyz.donot.roselinx.ui.util.extraUtils.mainThread
import xyz.donot.roselinx.ui.util.extraUtils.toast
import xyz.donot.roselinx.ui.util.extraUtils.twitterExceptionMessage
import xyz.donot.roselinx.ui.util.getAccount
import xyz.donot.roselinx.ui.view.SingleLiveEvent

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    var query: MutableLiveData<Query> = MutableLiveData()
    val exception = MutableLiveData<TwitterException>()
    val mainTwitter by lazy { getAccount() }
    val dataRefreshed = SingleLiveEvent<Unit>()
    val adapter: BaseQuickAdapter<Status, BaseViewHolder> by lazy { StatusAdapter() }
    val data = MutableLiveData<List<Status>>()
    fun pullDown() {
        if (adapter.data.isNotEmpty()) {
            launch(UI) {
                adapter.data.clear()
                adapter.notifyDataSetChanged()
                loadMoreData()
                dataRefreshed.call()
            }
        } else {
            dataRefreshed.call()
        }
    }

    private fun endAdapter() = mainThread {
        adapter.loadMoreEnd(true)
    }

    fun loadMoreData() {
        launch(UI) {
            try {
                val result = async(CommonPool) { mainTwitter.account.search(query.value) }.await()
                if (result.hasNext()) {
                    query.value = result.nextQuery()
                    adapter.loadMoreComplete()
                } else {
                    endAdapter()
                }
                adapter.addData(result.tweets)
            } catch (e: TwitterException) {
                getApplication<Roselin>().toast(twitterExceptionMessage(e))

            }
        }

    }
}