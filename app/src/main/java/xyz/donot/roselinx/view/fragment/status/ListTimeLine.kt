package xyz.donot.roselinx.view.fragment.status

import android.os.Bundle
import android.view.View
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import twitter4j.Paging


class ListTimeLine:TimeLineFragment(){
    private val listId by lazy { arguments.getLong("listId") }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.pullToRefresh= {twitter->
            async(CommonPool){twitter.getUserListStatuses(listId,Paging(viewmodel.adapter.data[0].id))}
        }
        viewmodel.getData= {twitter->
            async(CommonPool){ twitter.getUserListStatuses(listId, Paging(page))}
        }
    }
}
