package xyz.donot.roselin.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_base_fragment.*
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.async
import xyz.donot.roselin.util.extraUtils.mainThread
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.view.adapter.UserListAdapter
import xyz.donot.roselin.view.custom.MyLoadingView

class UserListFragment:Fragment()
{
    private var cursor: Long = -1L
    private val friend by lazy { arguments.getBoolean("isFriend") }
    private val userId by lazy { arguments.getLong("userId")}
    val twitter by lazy { getTwitterInstance() }
    val adapter by lazy { UserListAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.content_base_fragment, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dividerItemDecoration = DividerItemDecoration( recycler.context, LinearLayoutManager(activity).orientation)
        recycler.addItemDecoration(dividerItemDecoration)
        recycler.layoutManager = LinearLayoutManager(activity)
        adapter.setOnLoadMoreListener({loadMore()},recycler)
        adapter.setLoadMoreView(MyLoadingView())
        adapter.setHasStableIds(true)
        recycler.adapter=adapter
        refresh.isEnabled=false
        loadMore()

    }

    private fun loadMore() = async {
        try {
          val result= if (friend){ twitter.getFriendsList(userId,cursor)}
            else{ twitter.getFollowersList(userId,cursor)}
            mainThread {
                if(result!=null){
                    adapter.addData(result)
                if (result.hasNext()){ cursor=result.nextCursor}
                    adapter.loadMoreComplete()
               }
            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }

    }
}