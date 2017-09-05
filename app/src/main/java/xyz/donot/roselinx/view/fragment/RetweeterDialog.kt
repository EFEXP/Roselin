package xyz.donot.roselinx.view.fragment



import android.os.Bundle
import android.view.View
import twitter4j.User
import xyz.donot.roselinx.util.extraUtils.intent
import xyz.donot.roselinx.util.extraUtils.mainThread
import xyz.donot.roselinx.view.activity.UserActivity
import xyz.donot.roselinx.view.adapter.UserListAdapter
import xyz.donot.roselinx.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselinx.view.custom.MyViewHolder

class RetweeterDialog : BaseListFragment<User>() {
    private var cursor: Long = -1L
    override fun adapterFun(): MyBaseRecyclerAdapter<User, MyViewHolder> = UserListAdapter()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.setOnItemClickListener { _, _, position ->
            val intent=activity.intent<UserActivity>()
            intent.putExtra("user_id",adapter.getItem(position))
            activity.startActivity(intent)
            adapter.getItem(position)
        }
    }

    override fun pullToRefresh(adapter: MyBaseRecyclerAdapter<User, MyViewHolder>) = Unit

    override fun GetData(): MutableList<User>? {
        val result=twitter.getRetweeterIds(tweetId, cursor)
        val users=twitter.users().lookupUsers(*result.iDs)
        return if (users != null) {
            mainThread {
                if (result.hasNext()){cursor=result.nextCursor}
                else{
                    adapter.loadMoreComplete()
                    shouldLoad=false
                    }
            }
            users
        }
        else{
            shouldLoad=false
            null
        }

    }

    val tweetId by lazy { arguments.getLong("tweetId") }



}
