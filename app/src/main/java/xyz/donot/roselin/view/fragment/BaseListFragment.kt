@file:Suppress("UNCHECKED_CAST")

package xyz.donot.roselin.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.content_base_fragment.*
import xyz.donot.roselin.R
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.view.custom.MyLoadingView

abstract class BaseListFragment<T> : Fragment() {
    val twitter by lazy { getTwitterInstance() }
    val adapter by lazy { adapterFun() }

    abstract fun adapterFun():BaseQuickAdapter<T,BaseViewHolder>
    abstract fun loadMore(adapter: BaseQuickAdapter<T, BaseViewHolder>)
    abstract fun pullToRefresh(adapter: BaseQuickAdapter<T, BaseViewHolder>)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.content_base_fragment, container, false)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dividerItemDecoration = DividerItemDecoration( recycler.context,
                LinearLayoutManager(activity).orientation)
        recycler.addItemDecoration(dividerItemDecoration)
        recycler.layoutManager = LinearLayoutManager(activity)
        // adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
        adapter.setOnLoadMoreListener({ loadMore(adapter)},recycler)
        adapter.setLoadMoreView(MyLoadingView())
        // adapter.emptyView=View.inflate(activity, R.layout.item_empty,null)
        recycler.adapter=adapter
      if (savedInstanceState==null){loadMore(adapter)}
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
                loadMore(adapter)
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


}
