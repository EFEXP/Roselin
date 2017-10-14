package xyz.donot.roselinx.ui.dialog

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.model.entity.TwitterAccount
import xyz.donot.roselinx.model.entity.UserData
import xyz.donot.roselinx.ui.base.ARecyclerFragment
import xyz.donot.roselinx.ui.userlist.TwitterUserPreAdapter
import xyz.donot.roselinx.ui.util.extraUtils.bundle
import xyz.donot.roselinx.ui.util.getAccount

class RetweetUserDialog : ARecyclerFragment() {
    val viewmodel: RetweetUserViewModel by lazy { ViewModelProviders.of(this).get(RetweetUserViewModel::class.java) }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.tweetId = arguments.getLong("tweetId")
        val adapter = TwitterUserPreAdapter()
        adapter.apply {
            onLoadMore = {
                viewmodel.onLoadMore()
            }
        }
        recycler.adapter = adapter
        viewmodel.userList.observe(this, Observer {
            it?.let {
                adapter.itemList=it
            }
        })
        viewmodel.onLoadMore()
    }

    companion object {
        fun getInstance(tweetId: Long): RetweetUserDialog {
            return RetweetUserDialog().apply {
                arguments = bundle { putLong("tweetId", tweetId) }
            }
        }

    }

}

class RetweetUserViewModel : ViewModel() {
    var cursor: Long = -1L
    var tweetId = 0L
    var enableLoadMore = true
    val twitter: TwitterAccount by lazy { getAccount() }
    val userList: MutableLiveData<ArrayList<UserData>> = MutableLiveData()
    fun onLoadMore() {
        if (enableLoadMore)
            launch(UI) {
                val result = async { twitter.account.getRetweeterIds(tweetId, cursor) }.await()
                if (result.hasNext()) {
                    cursor = result.nextCursor
                } else {
                    enableLoadMore = false
                }
                if (result.iDs.isEmpty()) {
                    enableLoadMore = false
                } else {
                    val users: List<UserData> = async { twitter.account.users().lookupUsers(*result.iDs).map { UserData(id = it.id, user = it, screenname = it.screenName) } }.await()
                    val list=(userList.value ?: ArrayList())
                    list.addAll(users)
                    userList.value=list }
            }
    }


}