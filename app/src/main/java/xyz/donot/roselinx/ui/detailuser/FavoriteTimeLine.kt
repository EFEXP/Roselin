package xyz.donot.roselinx.ui.detailuser

import android.os.Bundle
import android.view.View
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import twitter4j.Paging
import xyz.donot.roselinx.ui.base.TimeLineFragment


class FavoriteTimeLine : TimeLineFragment() {
    val userId by lazy { arguments.getLong("userId") }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.pullToRefresh = {
            async(CommonPool) { it.getFavorites(userId, Paging(viewmodel.adapter!!.data[0].id)) }
        }
        viewmodel.getData = { twitter ->
            async(CommonPool) { twitter.getFavorites(userId, Paging(viewmodel.page)) }
        }
    }
    companion object {
        fun newInstance(userId:Long): FavoriteTimeLine {
            return FavoriteTimeLine().apply {
               arguments=xyz.donot.roselinx.util.extraUtils.Bundle {  putLong("userId",userId) }
            }
        }

    }

}

