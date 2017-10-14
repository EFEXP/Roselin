package xyz.donot.roselinx.ui.main

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import xyz.donot.roselinx.model.entity.*
import xyz.donot.roselinx.ui.dialog.TrendFragment
import xyz.donot.roselinx.ui.directmassage.DMListFragment
import xyz.donot.roselinx.ui.home.HomeTimeLineFragment
import xyz.donot.roselinx.ui.mention.MentionTimeLine
import xyz.donot.roselinx.ui.search.SearchTimeline
import xyz.donot.roselinx.ui.util.extraUtils.bundle
import xyz.donot.roselinx.ui.util.getSerialized


class MainTimeLineAdapter(fm: FragmentManager, private val tabSetting: List<SavedTab>) : FragmentPagerAdapter(fm) {
    override fun getItem(i: Int): Fragment {
        return when (tabSetting[i].type) {
            HOME -> HomeTimeLineFragment().apply {
                val tw = RoselinDatabase.getAllowedInstance().twitterAccountDao().findById(tabSetting[i].accountId)
                arguments = bundle  { putByteArray("twitter", tw.account.getSerialized()) }
            }
            MENTION ->
                MentionTimeLine().apply {

                    val tw = RoselinDatabase.getAllowedInstance().twitterAccountDao().findById(tabSetting[i].accountId)
                    arguments =bundle { putByteArray("twitter", tw.account.getSerialized()) }

                }


            SEARCH -> SearchTimeline().apply {
                arguments = bundle  {
                    putString("query_text", tabSetting[i].searchWord)
                    putByteArray("query_bundle", tabSetting[i].searchQuery?.getSerialized())
                }
            }
            LIST -> ListTimeLine().apply {

                val tw = RoselinDatabase.getAllowedInstance().twitterAccountDao().findById(tabSetting[i].accountId)
                arguments =bundle  {
                    putLong("listId", tabSetting[i].listId)
                    putByteArray("twitter", tw.account.getSerialized())
                }


            }
            NOTIFICATION -> NotificationFragment()
            TREND -> TrendFragment()
            DM -> DMListFragment().apply {
                val tw = RoselinDatabase.getAllowedInstance().twitterAccountDao().findById(tabSetting[i].accountId)
                arguments = bundle { putByteArray("twitter", tw.account.getSerialized()) }

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
