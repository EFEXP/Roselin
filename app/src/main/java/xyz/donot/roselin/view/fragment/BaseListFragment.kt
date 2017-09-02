@file:Suppress("UNCHECKED_CAST")

package xyz.donot.roselin.view.fragment

import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Twitter
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.util.getDeserialized
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselin.view.custom.MyLoadingView
import xyz.donot.roselin.view.custom.MyViewHolder

abstract class BaseListFragment<T> : AppCompatDialogFragment() {
    val twitter by lazy {
        if (arguments!=null&&arguments.containsKey("twitter")){
        return@lazy    arguments.getByteArray("twitter").getDeserialized<Twitter>()
        }else
        return@lazy     getTwitterInstance() }
    val main_twitter by lazy { getTwitterInstance() }
    val adapter by lazy { adapterFun() }
    var useDefaultLoad=true
    var shouldLoad =true
    set(value) {
        if (!value){
            adapter.loadMoreComplete()
            adapter.loadMoreEnd()
        }
        field=value
    }
    private var isBackground=false
    private val dataStore:ArrayList<T> =ArrayList()
    abstract fun adapterFun():MyBaseRecyclerAdapter<T,MyViewHolder>
    open   fun pullToRefresh(adapter: MyBaseRecyclerAdapter<T, MyViewHolder>) = Unit
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.content_base_fragment, container, false)
    fun insertDataBackground(data:List<T>){
        if(!isBackground){
            val  positionIndex =  (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            adapter.addData(0,data)
            if (positionIndex==0) {
                recycler.layoutManager.scrollToPosition(0)
            }
        }
        else{dataStore.addAll(0,data)}
    }
    fun insertDataBackground(data:T){
        if(!isBackground){
            val  positionIndex =  (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            adapter.addData(0,data)
            if (positionIndex==0) {
                recycler.layoutManager.scrollToPosition(0)
            }
        }
        else{dataStore.add(0,data)}
    }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dividerItemDecoration = DividerItemDecoration( recycler.context, LinearLayoutManager(activity).orientation)
        adapter.apply {
            setOnLoadMoreListener({
                if (shouldLoad){ if (useDefaultLoad){LoadMoreData()} else{LoadMoreData2()} } },recycler)
            setLoadMoreView(MyLoadingView())
           emptyView=View.inflate(activity, R.layout.item_empty,null)
        }
        recycler.apply {
            (itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(dividerItemDecoration)

        }
        recycler.adapter= adapter
      if (savedInstanceState==null){
          if (useDefaultLoad){LoadMoreData()}
          else{LoadMoreData2()}
      }
        else{
          val t=savedInstanceState.getSerializable("data") as ArrayList<T>
          Log.d("SavedInstanceStateHas",t.size.toString())
          adapter.addData(t)
      }

        refresh.setOnRefreshListener {
            if (adapter.data.isNotEmpty()){
                pullToRefresh(adapter)
               refresh.isRefreshing=false
            }
            else{
               if (useDefaultLoad){LoadMoreData()}
                else{LoadMoreData2()}
                refresh.isRefreshing=false
            } }


    }

    private fun returnDataAsync() = async(CommonPool) {
        return@async GetData()
    }


    override fun onResume() {
        super.onResume()
        isBackground=false
        if(dataStore.isNotEmpty()) {
            adapter.addData(0, dataStore)
            val positionIndex = (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            if (positionIndex == 0) recycler.layoutManager.scrollToPosition(0)
            dataStore.clear()
        }
    }

    override fun onStop() {
        super.onStop()
        isBackground=true
    }
    open  fun LoadMoreData2() = Unit
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
       val l=ArrayList<T>()
        l.addAll(adapter.data)
        Log.d("GiveData",adapter.data.count().toString())
        outState?.putSerializable("data",l)
    }
    abstract fun GetData(): MutableList<T>?
    private fun LoadMoreData(){  launch(UI){
        try {
            val result =returnDataAsync().await()
            adapter.loadMoreComplete()
            result?.let {
                adapter.addData(result)
            }
        } catch (e: Exception) {
            adapter.loadMoreFail()
            toast(e.localizedMessage)
        }
    }
    }

}

