package xyz.donot.roselin.view.fragment



import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import twitter4j.User
import xyz.donot.roselin.util.extraUtils.mainThread
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.view.adapter.UserListAdapter

class RetweeterDialog : BaseListFragment<User>() {
    private var cursor: Long = -1L
    override fun adapterFun(): BaseQuickAdapter<User, BaseViewHolder> = UserListAdapter()

    override fun pullToRefresh(adapter: BaseQuickAdapter<User, BaseViewHolder>) {

    }

    override fun GetData(): MutableList<User>? {
        val result=twitter.getRetweeterIds(tweetId, cursor)
        val users=twitter.users().lookupUsers(*result.iDs)
        toast(result.iDs[0].toString())
        return if (users != null) {
            mainThread {
                if (result.hasNext()){cursor=result.nextCursor}
                else{
                    shouldLoad=false
                    }
            }
            users
        }
        else{
            null
        }

    }

    val tweetId by lazy { arguments.getLong("tweetId") }



}
