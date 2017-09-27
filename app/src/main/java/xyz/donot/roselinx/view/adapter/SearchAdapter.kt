package xyz.donot.roselinx.view.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import twitter4j.PagableResponseList
import twitter4j.Query
import twitter4j.User
import xyz.donot.roselinx.util.extraUtils.Bundle
import xyz.donot.roselinx.util.extraUtils.intent
import xyz.donot.roselinx.util.extraUtils.mainThread
import xyz.donot.roselinx.util.getSerialized
import xyz.donot.roselinx.view.activity.UserActivity
import xyz.donot.roselinx.view.fragment.TrendFragment
import xyz.donot.roselinx.view.fragment.status.SearchTimeline
import xyz.donot.roselinx.view.fragment.user.UserListFragment

class SearchAdapter(private val query: Query, private val queryText: String, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = when (position) {
        0 -> {
            SearchTimeline().apply { arguments = Bundle { putByteArray("query_bundle", query.getSerialized()) } }
        }
        1 -> {
            UserSearch().apply { arguments = Bundle { putString("query_text", queryText) } }
        }
        2 -> TrendFragment()

        else -> throw  IllegalStateException()
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "Tweet"
        1 -> "User"
        2 -> "Trend"
        else -> throw  IllegalStateException()
    }

    override fun getCount(): Int = 3

    class UserSearch : UserListFragment() {
        private var page: Int = 0
            get() {
                field++
                return field
            }

        override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            viewmodel.adapter!!.setOnItemClickListener { _, _, position ->
                val intent = activity.intent<UserActivity>()
                intent.putExtra("user_id", viewmodel.adapter!!.getItem(position)?.id)
                activity.startActivity(intent)
                viewmodel.adapter!!.getItem(position)

            }
        }

        override fun getUserData(userId: Long, cursor: Long): PagableResponseList<User>? {
            val queryText = arguments.getString("query_text")
            val result = viewmodel.twitter.searchUsers(queryText, page)
            mainThread {
                if (result != null) {
                    viewmodel.adapter!!.addData(result)
                }
            }
            return null
        }
    }

}

