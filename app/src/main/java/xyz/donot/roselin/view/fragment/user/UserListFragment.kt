package xyz.donot.roselin.view.fragment.user

import android.os.Bundle
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.PagableResponseList
import twitter4j.User
import xyz.donot.roselin.util.extraUtils.async
import xyz.donot.roselin.util.extraUtils.mainThread
import xyz.donot.roselin.view.adapter.UserListAdapter
import xyz.donot.roselin.view.fragment.BaseListFragment

abstract class UserListFragment:BaseListFragment<User>()
{
    private var cursor: Long = -1L
    private val userId by lazy { arguments.getLong("userId")}
    abstract fun getUserData(userId:Long,cursor:Long): PagableResponseList<User>?
    override fun pullToRefresh(adapter: BaseQuickAdapter<User, BaseViewHolder>) {}
    override fun adapterFun(): BaseQuickAdapter<User, BaseViewHolder> =UserListAdapter()
    override fun loadMore(adapter: BaseQuickAdapter<User, BaseViewHolder>) {
        async {
            try {
                val result =getUserData(userId,cursor)
                        mainThread {
                            if (result != null) {
                                adapter.addData(result)
                                if (result.hasNext()) {
                                    cursor = result.nextCursor
                                }
                                else{
                                    adapter.loadMoreEnd()
                                }
                                adapter.loadMoreComplete()
                            }
                        }
            }
            catch (e:Exception){
                e.printStackTrace()
                adapter.loadMoreFail()
            }

        }
    }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refresh.isEnabled=false
        if (savedInstanceState!=null)
            cursor=savedInstanceState.getLong("cursor",-1L)
    }
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putLong("cursor",cursor)
    }


}