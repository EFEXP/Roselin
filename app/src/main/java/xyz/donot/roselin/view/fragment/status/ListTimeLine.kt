package xyz.donot.roselin.view.fragment.status

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.Paging
import twitter4j.Status
import xyz.donot.roselin.util.extraUtils.async
import xyz.donot.roselin.util.extraUtils.mainThread
import xyz.donot.roselin.util.extraUtils.toast


class ListTimeLine:TimeLineFragment(){
    private val listId by lazy { arguments.getLong("listId") }
    override fun loadMore(adapter: BaseQuickAdapter<Status, BaseViewHolder>) {
        async {
            try {
                val result=     twitter.getUserListStatuses(listId, Paging(page))
                if (result!=null)
                {
                    mainThread {
                        adapter.addData(result)
                        adapter.loadMoreComplete()
                    }
                }
            } catch (e: Exception) {
                toast(e.localizedMessage)
            }

        }

    }

    override fun pullToRefresh(adapter: BaseQuickAdapter<Status, BaseViewHolder>) {
        async {
            try {
                val result= twitter.getUserListStatuses(listId,Paging(adapter.data[0].id))
                if (result.isNotEmpty()){
                    mainThread {
                        adapter.addData(0,result)
                        recycler.smoothScrollToPosition(0) }
                }
            }
            catch (e:Exception){ toast(e.localizedMessage)}
        }
    }
}