package xyz.donot.roselinx.viewmodel.fragment

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import twitter4j.Twitter
import xyz.donot.roselinx.util.getTwitterInstance
import xyz.donot.roselinx.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselinx.view.custom.MyViewHolder
import kotlin.properties.Delegates


class BaseListViewModel<T>(app: Application) : ARecyclerViewModel(app) {
    var isBackground = MutableLiveData<Boolean>()
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
            if (!value) {
                adapter.loadMoreComplete()
                adapter.loadMoreEnd()
            }
            field = value
        }

    private fun insertDataBackground(data: List<T>) {
        isBackground.let {
            if (!isBackground.value!!) {
                adapter.addData(0, data)
                dataInserted.value = Unit
            } else {
                dataStore.addAll(0, data)
            }
        }
    }


    fun insertDataBackground(data: T) {
        isBackground.let {
            if (!isBackground.value!!) {
                adapter.addData(0, data)
                dataInserted.value = Unit
            } else {
                dataStore.add(0, data)
            }
        }
    }

    var  pullToRefresh :(Twitter)-> Deferred<List<T>>? by Delegates.notNull()

    fun pullDown() {
        if (adapter.data.isNotEmpty()) {
            launch(UI){
              pullToRefresh(twitter)?.await()?.let { insertDataBackground(it) }
                dataRefreshed
            }

        } else {
            dataRefreshed
        }
    }

    fun initAdapter(adapter_: MyBaseRecyclerAdapter<T, MyViewHolder>) {
        adapter = adapter_
    }

    override fun onCleared() {
        super.onCleared()

    }

}
