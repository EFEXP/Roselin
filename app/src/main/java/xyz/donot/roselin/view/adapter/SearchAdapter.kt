package xyz.donot.roselin.view.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import twitter4j.PagableResponseList
import twitter4j.Query
import twitter4j.User
import xyz.donot.roselin.util.extraUtils.Bundle
import xyz.donot.roselin.util.extraUtils.intent
import xyz.donot.roselin.util.extraUtils.mainThread
import xyz.donot.roselin.util.getSerialized
import xyz.donot.roselin.view.activity.UserActivity
import xyz.donot.roselin.view.fragment.TrendFragment
import xyz.donot.roselin.view.fragment.status.SearchTimeline
import xyz.donot.roselin.view.fragment.user.UserListFragment

class SearchAdapter( private val query: Query, private val queryText:String, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = when(position){
        0->
        {
           SearchTimeline().apply { arguments= Bundle { putByteArray("query_bundle",query.getSerialized()) } }

        }
        1 ->  { UserSearch().apply {arguments= Bundle { putString("query_text",queryText) }  } }
      2-> TrendFragment()

        else->throw  IllegalStateException()
    }
    override fun getPageTitle(position: Int): CharSequence = when(position){
        0->"Tweet"
        1->"User"
        2->"Trend"
        else->throw  IllegalStateException()
    }

    override fun getCount(): Int = 3

    class UserSearch:UserListFragment(){
        private var page: Int = 0
            get() {
                field++
                return field
            }
        override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            adapter.setOnItemClickListener { _, _, position ->
                val intent=activity.intent<UserActivity>()
                intent.putExtra("user_id",adapter.getItem(position))
                activity.startActivity(intent)
                adapter.getItem(position)

            }
        }

        override fun getUserData(userId: Long, cursor: Long): PagableResponseList<User>? {
            val queryText=arguments.getString("query_text")
         val  result= twitter.searchUsers(queryText,page)
            mainThread {
            if (result != null) {
                adapter.addData(result)
            }

            }
            return null
        } }

}

