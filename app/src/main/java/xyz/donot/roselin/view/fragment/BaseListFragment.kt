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
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.ResponseList
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.async
import xyz.donot.roselin.util.extraUtils.mainThread
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.view.custom.MyLoadingView

abstract class BaseListFragment<T> : AppCompatDialogFragment() {
    val twitter by lazy { getTwitterInstance() }
    val adapter by lazy { adapterFun() }

    abstract fun adapterFun():BaseQuickAdapter<T,BaseViewHolder>
    abstract fun pullToRefresh(adapter: BaseQuickAdapter<T, BaseViewHolder>)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.content_base_fragment, container, false)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dividerItemDecoration = DividerItemDecoration( recycler.context,
                LinearLayoutManager(activity).orientation)
        recycler.addItemDecoration(dividerItemDecoration)
        recycler.layoutManager = LinearLayoutManager(activity)
        // adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
        adapter.setOnLoadMoreListener({ LoadMoreData()},recycler)
        adapter.setLoadMoreView(MyLoadingView())
        // adapter.emptyView=View.inflate(activity, R.layout.item_empty,null)
        recycler.adapter=adapter
      if (savedInstanceState==null){LoadMoreData()}
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
               LoadMoreData()
                refresh.isRefreshing=false
            } }

    }
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
       val l=ArrayList<T>()
        l.addAll(adapter.data)
        Log.d("GiveData",adapter.data.count().toString())
        outState?.putSerializable("data",l)
    }
    abstract fun GetData(): ResponseList<T>?
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

