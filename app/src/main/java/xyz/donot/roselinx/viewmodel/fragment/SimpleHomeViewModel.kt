package xyz.donot.roselinx.viewmodel.fragment

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Paging
import twitter4j.Status
import twitter4j.Twitter
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.util.extraUtils.toast
import xyz.donot.roselinx.view.adapter.SimpleBaseAdapter
import xyz.donot.roselinx.view.adapter.SimpleStatusAdapter

class SimpleHomeViewModel(app: Application) : AndroidViewModel(app) {
    var list: ArrayList<Status> = ArrayList()
    val isLoading: MutableLiveData<Boolean> =MutableLiveData()
    lateinit var twitter: Twitter
    val adapter = SimpleStatusAdapter(list, app)
    var page: Int = 0
        get() {
            field++
            return field
        }
    init {
        adapter.onLoadMore = object : SimpleBaseAdapter.LoadMoreListener {
            override fun onLoadMore() {
                isLoading.value=true
                loadMore()
            }
        }
        adapter.itemClickListener = object : SimpleBaseAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
              getApplication<Roselin>().toast(list[position].text)




            }

        }

    }

    fun loadMore() {
        launch(UI) {
            val result = async(CommonPool) {
                twitter.getHomeTimeline(Paging(page))
            }.await()
            adapter.addData(result)
            isLoading.value=false
        }
    }

    fun refresh() {}

}
