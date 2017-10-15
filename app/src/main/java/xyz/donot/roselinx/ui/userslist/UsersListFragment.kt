package xyz.donot.roselinx.ui.userslist


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
import xyz.donot.roselinx.ui.base.ARecyclerFragment
import xyz.donot.roselinx.ui.main.ListTimeLine
import xyz.donot.roselinx.ui.util.extraUtils.bundle
import xyz.donot.roselinx.ui.util.extraUtils.mainThread
import xyz.donot.roselinx.ui.util.getAccount
import xyz.donot.roselinx.ui.view.DynamicViewPager
import xyz.donot.roselinx.ui.view.MyLoadingView


class UsersListFragment : ARecyclerFragment() {
    private val activityViewmodel: UsersListActivityViewModel by lazy { ViewModelProviders.of(activity).get(UsersListActivityViewModel::class.java) }
    private val viewmodel: UsersListViewModel by lazy { ViewModelProviders.of(this).get(UsersListViewModel::class.java) }

    companion object {
        fun newInstance(userId: Long, isAddedList: Boolean): UsersListFragment {
            return UsersListFragment()
                    .apply {
                        arguments = bundle  {
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
                    ListTimeLine().apply { arguments = bundle { putLong("listId", item.id) } }.show(fragmentManager, "")
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


