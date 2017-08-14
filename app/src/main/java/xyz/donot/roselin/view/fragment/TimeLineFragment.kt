package xyz.donot.roselin.view.fragment


import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
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

abstract class TimeLineFragment : AppCompatDialogFragment() {
    val twitter by lazy { getTwitterInstance() }
    val adapter by lazy { StatusAdapter(activity, mutableListOf()) }
    abstract fun loadMore(adapter:BaseQuickAdapter<Status,BaseViewHolder>)
    abstract fun   pullToRefresh(adapter:BaseQuickAdapter<Status,BaseViewHolder>)
    var page: Int = 0
        get() {
            field++
            return field
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.content_base_fragment, container, false)
    }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler.layoutManager = LinearLayoutManager(activity)
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
        adapter.setOnLoadMoreListener({  loadMore(adapter) },recycler)
        adapter.setLoadMoreView(MyLoadingView())
        adapter.emptyView=View.inflate(activity, R.layout.item_empty,null)
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
    }
}
