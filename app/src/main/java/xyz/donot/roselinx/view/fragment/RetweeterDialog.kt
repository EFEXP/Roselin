package xyz.donot.roselinx.view.fragment


import android.os.Bundle
import android.view.View
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import twitter4j.User
import xyz.donot.roselinx.util.extraUtils.intent
import xyz.donot.roselinx.view.activity.UserActivity
import xyz.donot.roselinx.view.adapter.UserListAdapter
import xyz.donot.roselinx.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselinx.view.custom.MyViewHolder

class RetweeterDialog : BaseListFragment<User>() {
    private var cursor: Long = -1L
    override fun adapterFun(): MyBaseRecyclerAdapter<User, MyViewHolder> = UserListAdapter()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.adapter.setOnItemClickListener { _, _, position ->
            val intent = activity.intent<UserActivity>()
            intent.putExtra("user_id", viewmodel.adapter.getItem(position)?.id)
            activity.startActivity(intent)
            viewmodel.adapter.getItem(position)
        }

        viewmodel.getData = { twitter ->
                async(CommonPool) {
                    val result = twitter.getRetweeterIds(tweetId, cursor)
                    if (result.hasNext()) {
                        cursor = result.nextCursor
                    } else {
                        viewmodel.shouldLoad = false
                    }
                    if (result.iDs.isEmpty()) {
                        null
                    } else
                    twitter.users().lookupUsers(*result.iDs)
                }
        }

    }


    private val tweetId by lazy { arguments.getLong("tweetId") }


}
