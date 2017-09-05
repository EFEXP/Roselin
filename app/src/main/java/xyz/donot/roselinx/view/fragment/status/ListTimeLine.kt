package xyz.donot.roselinx.view.fragment.status

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Paging
import twitter4j.ResponseList
import twitter4j.Status
import xyz.donot.roselinx.util.extraUtils.twitterExceptionToast
import xyz.donot.roselinx.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselinx.view.custom.MyViewHolder


class ListTimeLine:TimeLineFragment(){
    private val listId by lazy { arguments.getLong("listId") }
    override fun GetData(): ResponseList<Status>? =  twitter.getUserListStatuses(listId, Paging(page))
    override fun pullToRefresh(adapter: MyBaseRecyclerAdapter<Status, MyViewHolder>) {
        launch(UI){
            try {
                val result=    async(CommonPool){twitter.getUserListStatuses(listId,Paging(adapter.data[0].id))}.await()
                    insertDataBackground(result)
                }
            catch (e:Exception){
                activity.twitterExceptionToast(e)
            }
        }

    }
}
