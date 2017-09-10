package xyz.donot.roselinx.viewmodel.fragment

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import twitter4j.Twitter
import xyz.donot.roselinx.util.extraUtils.mainThread
import xyz.donot.roselinx.util.getTwitterInstance
import xyz.donot.roselinx.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselinx.view.custom.MyViewHolder
import kotlin.properties.Delegates


class BaseListViewModel<T>(app: Application) : ARecyclerViewModel(app) {
    var isBackground = MutableLiveData<Boolean>().apply { value=false }
    var twitter by Delegates.notNull<Twitter>()
    val main_twitter by lazy { getTwitterInstance() }
    val dataInserted = MutableLiveData<Unit>()
    val dataRefreshed = MutableLiveData<Unit>()
    val dataStore: ArrayList<T> = ArrayList()
    var adapter by Delegates.notNull<MyBaseRecyclerAdapter<T, MyViewHolder>>()
    val data = MutableLiveData<List<T>>()
    var useDefaultLoad = true
    var shouldLoad = true
        set(value) {
            if (!value){
                    adapter.loadMoreEnd()
                    adapter.loadMoreComplete()
            }
            field = value
        }

    private fun insertDataBackground(data: List<T>) {
        mainThread {
                if (isBackground.value!!) {
                    dataStore.addAll(0, data)
                } else {
                    adapter.addData(0, data)
                    dataInserted.value = Unit
                }
        }
    }


    fun insertDataBackground(data: T) {
        mainThread {
                if (isBackground.value!!) {
                    dataStore.add(0, data)
                } else {
                    adapter.addData(0, data)
                    dataInserted.value = Unit
            }
        }
    }

    var pullToRefresh: (Twitter) -> Deferred<List<T>>? by Delegates.notNull()

    fun pullDown() {
        if (adapter.data.isNotEmpty()) {
            launch(UI) {
                pullToRefresh(twitter)?.await()?.let { insertDataBackground(it) }
                dataRefreshed.value = Unit
            }
        } else {
            dataRefreshed.value = Unit
        }
    }

    fun initAdapter(adapter_: MyBaseRecyclerAdapter<T, MyViewHolder>) {
        adapter = adapter_
    }

    override fun onCleared() {
        super.onCleared()
    }

}
