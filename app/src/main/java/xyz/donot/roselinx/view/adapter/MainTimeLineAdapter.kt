package xyz.donot.roselinx.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import xyz.donot.roselinx.model.room.*
import xyz.donot.roselinx.util.extraUtils.Bundle
import xyz.donot.roselinx.util.getSerialized
import xyz.donot.roselinx.view.fragment.TrendFragment
import xyz.donot.roselinx.view.fragment.realm.NotificationFragment
import xyz.donot.roselinx.view.fragment.status.HomeTimeLineFragment
import xyz.donot.roselinx.view.fragment.status.ListTimeLine
import xyz.donot.roselinx.view.fragment.status.MentionTimeLine
import xyz.donot.roselinx.view.fragment.status.SearchTimeline
import xyz.donot.roselinx.view.fragment.user.DMListFragment
import xyz.donot.roselinx.view.fragment.user.RoselinFragment


class MainTimeLineAdapter(fm: FragmentManager, private val tabSetting: List<SavedTab>) : FragmentPagerAdapter(fm) {
    override fun getItem(i: Int): Fragment {
        return when (tabSetting[i].type) {
        //HomeTimeLineFragment()
            HOME -> HomeTimeLineFragment().apply {
                val tw = RoselinDatabase.getAllowedInstance().twitterAccountDao().findById(tabSetting[i].accountId)
                arguments = Bundle { putByteArray("twitter", tw.account.getSerialized()) }


            }
            MENTION ->
                MentionTimeLine().apply {

                    val tw = RoselinDatabase.getAllowedInstance().twitterAccountDao().findById(tabSetting[i].accountId)
                    arguments = Bundle { putByteArray("twitter", tw.account.getSerialized()) }

                }


            SEARCH -> SearchTimeline().apply {
                arguments = Bundle {
                    putString("query_text", tabSetting[i].searchWord)
                    putByteArray("query_bundle", tabSetting[i].searchQuery?.getSerialized())
                }
            }
            LIST -> ListTimeLine().apply {

                val tw = RoselinDatabase.getAllowedInstance().twitterAccountDao().findById(tabSetting[i].accountId)
                arguments = Bundle {
                    putLong("listId", tabSetting[i].listId)
                    putByteArray("twitter", tw.account.getSerialized())
                }


            }
            NOTIFICATION -> NotificationFragment()
            TREND -> TrendFragment()
            DM -> DMListFragment().apply {
                val tw = RoselinDatabase.getAllowedInstance().twitterAccountDao().findById(tabSetting[i].accountId)
                arguments = Bundle { putByteArray("twitter", tw.account.getSerialized()) }

            }
            SETTING -> RoselinFragment()
            else -> throw IllegalStateException()
        }

    }

    override fun getCount(): Int = tabSetting.size

    override fun getPageTitle(position: Int): CharSequence =
            if (tabSetting[position].screenName != null) "${toSimpleName(tabSetting[position].type)}@${tabSetting[position].screenName!!}"
            else toSimpleName(tabSetting[position].type)
}
