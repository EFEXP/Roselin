package xyz.donot.roselinx.ui.userslist

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_list.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.TwitterException
import twitter4j.UserList
import xyz.donot.roselinx.R
import xyz.donot.roselinx.ui.util.extraUtils.mainThread
import xyz.donot.roselinx.ui.util.getAccount

class UsersListViewModel(app: Application) : AndroidViewModel(app) {
    val adapter by lazy { UserListAdapter() }
    val twitter by lazy { getAccount() }
    val exception = MutableLiveData<TwitterException>()
    var userId: Long = 0
    var mode: Boolean = false
    var cursor: Long = -1
    fun loadMoreData() {
        launch(UI) {
            try {

                if (mode) {
                    val result = async(CommonPool) {twitter.account.getUserListMemberships(userId, cursor) }.await()
                    if (result.hasNext()) {
                        cursor = result.nextCursor
                        adapter.loadMoreComplete()
                    } else {
                        endAdapter()
                    }
                    adapter.addData(result)
                } else {
                    val result = async(CommonPool) { twitter.account.getUserLists(userId) }.await()
                    if (result.isEmpty()) {
                        endAdapter()
                    } else {
                        adapter.addData(result)
                        adapter.loadMoreComplete()
                    }
                }

            } catch (e: Exception) {
                adapter.loadMoreFail()
                e.printStackTrace()
                // exception.value = e
            }
        }
    }
    private fun endAdapter() = mainThread {
        adapter.loadMoreEnd(true)
    }
    inner class UserListAdapter : BaseQuickAdapter<UserList, BaseViewHolder>(R.layout.item_list) {
        override fun convert(helper: BaseViewHolder, item: UserList) {
            helper.getView<View>(R.id.item_list_root).apply {
                tv_author.text = item.user.name
                Picasso.with(context).load(item.user.biggerProfileImageURLHttps).into(iv_icon)
                tv_description.text = item.description
                tv_list_name.text = item.name
                tv_listed_user.text = "${item.memberCount}人のユーザー"
            }

        }
    }
}
