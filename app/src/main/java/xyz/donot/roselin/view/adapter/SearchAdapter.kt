package xyz.donot.roselin.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import twitter4j.Query
import xyz.donot.roselin.util.extraUtils.Bundle
import xyz.donot.roselin.util.getSerialized
import xyz.donot.roselin.view.fragment.SearchTweet

class SearchAdapter(val query: Query, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when(position){
            0->
            {
               SearchTweet().apply { arguments= Bundle { putByteArray("query_bundle",query.getSerialized()) } }

            }
         ///   1 ->  {
        //        val fragment= UserList1(query)
        //        fragment
         //   }
           // 2-> TrendFragment()

            else->throw  IllegalStateException()
        }}

 //   class UserList1(val query: Query) : UserList() {
     //   override fun loadMore() {
    //        twitterObservable.getUserSearchAsync(query.query, page).subscribe { mAdapter.addAll(it) }
    //    }
   // }
    override fun getPageTitle(position: Int): CharSequence {
        return when(position){
            0->"Tweet"
        //    1->"User"
     //       2->"Trend"
            else->throw  IllegalStateException()
        }}

    override fun getCount(): Int {
        return 1
    }
}

