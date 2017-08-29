@file:Suppress("UNCHECKED_CAST")

package xyz.donot.roselin.view.fragment

import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.animators.OvershootInRightAnimator
import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.Twitter
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.async
import xyz.donot.roselin.util.extraUtils.mainThread
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
   private var isBackground=false
    var useDefaultLoad=true
    var shouldLoad =true
    set(value) {
        if (!value){
            adapter.loadMoreComplete()
            adapter.loadMoreEnd()
        }
        field=value
    }
    private val dataStore=ArrayList<T>()
    abstract fun adapterFun():MyBaseRecyclerAdapter<T,MyViewHolder>
    open   fun pullToRefresh(adapter: MyBaseRecyclerAdapter<T, MyViewHolder>){}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.content_base_fragment, container, false)
    fun insertDataBackground(data:List<T>){
        if(!isBackground){
            val  positionIndex =  (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            adapter.addData(0,data)
            if (positionIndex==0) {
                (recycler).scrollToPosition(0)
            }
        }
        else{dataStore.addAll(0,data)}
    }
    fun insertDataBackground(data:T){
        if(!isBackground){
            val  positionIndex =  (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            adapter.addData(0,data)
            if (positionIndex==0) {
                (recycler).scrollToPosition(0)
            }
        }
        else{dataStore.add(0,data)}
    }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dividerItemDecoration = DividerItemDecoration( recycler.context,
                LinearLayoutManager(activity).orientation)
        adapter.apply {
            setOnLoadMoreListener({
                if (shouldLoad){ if (useDefaultLoad){LoadMoreData()} else{LoadMoreData2()} } },recycler)
            setLoadMoreView(MyLoadingView())
           emptyView=View.inflate(activity, R.layout.item_empty,null)
        }
        recycler.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(dividerItemDecoration)
            itemAnimator = OvershootInRightAnimator(1.0f)
        }
        recycler.adapter= AlphaInAnimationAdapter(adapter)
      if (savedInstanceState==null){
          if (useDefaultLoad){getInitialData()}
          else{getInitialData2()}
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

  open fun getInitialData2(){

    }

    private fun getInitialData(){
        async {
            try {
                val result=GetData()
                if (result!=null)
                {
                    mainThread {
                        adapter.setNewData(result)
                        adapter.loadMoreComplete()
                    }
                }
            } catch (e: Exception) {
                toast(e.localizedMessage)
                adapter.loadMoreFail()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        isBackground=false
        adapter.addData(0,dataStore)
        val  positionIndex =  (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        if (positionIndex==0) recycler.scrollToPosition(0)
        dataStore.clear()
    }

    override fun onStop() {
        super.onStop()
        isBackground=true
    }
    open  fun LoadMoreData2(){}
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
       val l=ArrayList<T>()
        l.addAll(adapter.data)
        Log.d("GiveData",adapter.data.count().toString())
        outState?.putSerializable("data",l)
    }
    abstract fun GetData(): MutableList<T>?
    private fun LoadMoreData(){
        async {
            try {
                val result=GetData()
                if (result!=null)
                {
                    mainThread {
                     adapter.addData(result)
                     adapter.loadMoreComplete()
                    }
                }
            } catch (e: Exception) {
                toast(e.localizedMessage)
                adapter.loadMoreFail()
            }

        }
    }

}

