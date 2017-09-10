package xyz.donot.roselinx.view.fragment.status

import android.os.Bundle
import android.view.View
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import twitter4j.Paging
import twitter4j.Status

class FavoriteTimeLine:TimeLineFragment(){
    val userId by lazy { arguments.getLong("userId")  }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.pullToRefresh={
            async(CommonPool){it.getFavorites(userId,Paging(viewmodel.adapter.data[0].id))}
        }
    }
    override fun GetData(): MutableList<Status> =viewmodel.twitter.getFavorites(userId, Paging(page))
}
