package xyz.donot.roselinx.ui.userlist

import android.os.Bundle
import android.view.View
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import twitter4j.PagableResponseList
import twitter4j.User
import xyz.donot.roselinx.ui.base.BaseListFragment

abstract class UserListFragment : BaseListFragment<User>() {
    private var cursor: Long = -1L
    private val userId by lazy { arguments!!.getLong("userId") }
    abstract fun getUserData(userId: Long, cursor: Long): PagableResponseList<User>?
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState==null)
            viewmodel.adapter= UserListAdapter()
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null)
            cursor = savedInstanceState.getLong("cursor", -1L)
        viewmodel.getData = { _ ->
            async(CommonPool) {
                val result = getUserData(userId, cursor)
                if (result != null) {
                    if (result.hasNext()) {
                        cursor = result.nextCursor
                    } else {
                        viewmodel.endAdapter()
                    }
                    result
                } else {
                    null
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("cursor", cursor)
    }


}
