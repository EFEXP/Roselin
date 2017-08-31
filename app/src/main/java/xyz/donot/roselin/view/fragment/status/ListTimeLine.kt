package xyz.donot.roselin.view.fragment.status

import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.Paging
import twitter4j.ResponseList
import twitter4j.Status
import xyz.donot.roselin.util.extraUtils.asyncDeprecated
import xyz.donot.roselin.util.extraUtils.mainThread
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselin.view.custom.MyViewHolder


class ListTimeLine:TimeLineFragment(){
    private val listId by lazy { arguments.getLong("listId") }
    override fun GetData(): ResponseList<Status>? =  twitter.getUserListStatuses(listId, Paging(page))
    override fun pullToRefresh(adapter: MyBaseRecyclerAdapter<Status, MyViewHolder>) {
        asyncDeprecated {
            try {
                val result= twitter.getUserListStatuses(listId,Paging(adapter.data[0].id))
                if (result.isNotEmpty()){
                    mainThread {
                        insertDataBackground(result)
                        recycler.smoothScrollToPosition(0) }
                }
            }
            catch (e:Exception){ toast(e.localizedMessage)}
        }
    }
}