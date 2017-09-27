package xyz.donot.roselinx.view.fragment.user


import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.gms.ads.AdRequest
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_ad.view.*
import kotlinx.android.synthetic.main.item_list.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.TwitterException
import twitter4j.UserList
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.Bundle
import xyz.donot.roselinx.util.extraUtils.mainThread
import xyz.donot.roselinx.util.getTwitterInstance
import xyz.donot.roselinx.view.activity.UsersListActivityViewModel
import xyz.donot.roselinx.view.custom.DynamicViewPager
import xyz.donot.roselinx.view.custom.MyLoadingView
import xyz.donot.roselinx.view.fragment.base.ARecyclerFragment
import xyz.donot.roselinx.view.fragment.status.ListTimeLine


class UsersListFragment : ARecyclerFragment() {
    private val activityViewmodel: UsersListActivityViewModel by lazy { ViewModelProviders.of(activity).get(UsersListActivityViewModel::class.java) }
    private val viewmodel: UsersListViewModel by lazy { ViewModelProviders.of(this).get(UsersListViewModel::class.java) }

    companion object {
        fun newInstance(userId: Long, isAddedList: Boolean): UsersListFragment {
            return UsersListFragment()
                    .apply {
                        arguments = xyz.donot.roselinx.util.extraUtils.Bundle {
                            putLong("userId", userId)
                            putBoolean("isAddedList", isAddedList)
                        }
                    }

        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.apply {
            adapter.apply {
                setOnLoadMoreListener({ viewmodel.loadMoreData() }, recycler)
                setLoadMoreView(MyLoadingView())
                emptyView = View.inflate(activity, R.layout.item_empty, null)
                if (savedInstanceState == null)
                    addHeaderView(View.inflate(activity, R.layout.item_ad, null).apply {
                        adView.loadAd(AdRequest.Builder()
                                .setGender(AdRequest.GENDER_MALE)
                                .addTestDevice("0CF83648F3E630518CF53907939C9A8D")
                                .addTestDevice("6D38172C5A30A07095F6420BC145C497")
                                .build())
                    })
            }
            userId = arguments.getLong("userId")
            mode = arguments.getBoolean("isAddedList")
            recycler.adapter = adapter
            if (savedInstanceState == null)
                viewmodel.loadMoreData()
            viewmodel.exception.observe(this@UsersListFragment, Observer {
                it?.let {
                    adapter.emptyView = View.inflate(activity, R.layout.item_no_content, null)
                }
            })
            viewmodel.adapter.setEnableLoadMore(false)
            viewmodel.adapter.setOnItemClickListener { _, _, position ->
                val item = viewmodel.adapter.data[position]
                if (activityViewmodel.isSelect) {
                    activityViewmodel.selectedList.value=item
                } else {
                    ListTimeLine().apply { arguments = Bundle { putLong("listId", item.id) } }.show(fragmentManager, "")
                }
            }
        }
    }


}

class UsersListPagerAdapter(fm: FragmentManager, val userId: Long) : DynamicViewPager(fm) {

    override fun getItem(i: Int): Fragment {
        return if (i == 0)
            UsersListFragment.newInstance(userId, true)
        else
            UsersListFragment.newInstance(userId, false)
    }

    override fun getCount(): Int = 2
    override fun getPageTitle(position: Int): CharSequence {
        return if (position == 0)
            "追加されているリスト"
        else
            "保存しているリスト"
    }

}

class UsersListViewModel(app: Application) : AndroidViewModel(app) {
    val adapter by lazy { UserListAdapter() }
    val twitter by lazy { getTwitterInstance() }
    val exception = MutableLiveData<TwitterException>()
    var userId: Long = 0
    var mode: Boolean = false
    var cursor: Long = -1

    fun loadMoreData() {
        launch(UI) {
            try {

                if (mode) {
                    val result = async(CommonPool) { twitter.getUserListMemberships(userId, cursor) }.await()
                    if (result.hasNext()) {
                        cursor = result.nextCursor
                        adapter.loadMoreComplete()
                    } else {
                        endAdapter()
                    }
                    adapter.addData(result)
                } else {
                    val result = async(CommonPool) { twitter.getUserLists(userId) }.await()
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
