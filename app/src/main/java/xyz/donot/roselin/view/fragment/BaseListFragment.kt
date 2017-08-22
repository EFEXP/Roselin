package xyz.donot.roselin.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.Status
import xyz.donot.roselin.R
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.view.adapter.StatusAdapter
import xyz.donot.roselin.view.custom.MyLoadingView

abstract class BaseListFragment : Fragment() {
    val twitter by lazy { getTwitterInstance() }
    val adapter by lazy { StatusAdapter() }

    var isEnabledRefresh:Boolean=false
    set(value) {
        refresh.isEnabled=value
        field=value
    }
    abstract fun loadMore(adapter: BaseQuickAdapter<Status, BaseViewHolder>)
    abstract fun pullToRefresh(adapter: BaseQuickAdapter<Status, BaseViewHolder>)
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
        loadMore(adapter)
        refresh.setOnRefreshListener {
            if (adapter.data.isNotEmpty()){
                pullToRefresh(adapter)
                refresh.isRefreshing=false
            }
            else{
                loadMore(adapter)
                refresh.isRefreshing=false
            } }
        isEnabledRefresh=refresh.isEnabled
    }



}

