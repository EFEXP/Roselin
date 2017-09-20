package xyz.donot.roselinx.model

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Paging
import xyz.donot.roselinx.model.realm.addHomeStatus
import xyz.donot.roselinx.util.getTwitterInstance


class StatusPageModel(private val mode: Timeline, val userId: Long = 0) {
    private val twitter by lazy { getTwitterInstance() }
    private var page: Int = 0
        get() {
            field++
            return field
        }

    fun loadMore() {
        launch(UI) {
            when (mode) {
                StatusPageModel.Timeline.Home -> {
                    val result = async(CommonPool) { twitter.getHomeTimeline(Paging(page)) }.await()
                    addHomeStatus(result)
                }
                StatusPageModel.Timeline.User -> {
                    val result = async(CommonPool) { twitter.getUserTimeline(userId, Paging(page)) }.await()
                }
            }


        }
    }


    enum class Timeline {
        Home, User
    }


}
