package xyz.donot.roselinx.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import io.realm.Realm
import xyz.donot.roselinx.model.realm.*
import xyz.donot.roselinx.util.extraUtils.Bundle
import xyz.donot.roselinx.view.fragment.TrendFragment
import xyz.donot.roselinx.view.fragment.realm.NotificationFragment
import xyz.donot.roselinx.view.fragment.status.HomeTimeLineFragment
import xyz.donot.roselinx.view.fragment.status.ListTimeLine
import xyz.donot.roselinx.view.fragment.status.MentionTimeLine
import xyz.donot.roselinx.view.fragment.status.SearchTimeline
import xyz.donot.roselinx.view.fragment.user.DMListFragment
import xyz.donot.roselinx.view.fragment.user.RoselinFragment


class MainTimeLineAdapter(fm: FragmentManager, private val realmResults: List<TabDataObject>) : FragmentPagerAdapter(fm) {

    override fun getItem(i: Int): Fragment {
        Realm.getDefaultInstance().use { realm ->
                return when (realmResults[i].type) {
                    //HomeTimeLineFragment()
                    HOME -> HomeTimeLineFragment().apply {
                        val t = realm.where(AccountObject::class.java).equalTo("id", realmResults[i].accountId).findFirst()
                        arguments = Bundle { putByteArray("twitter", realm.copyFromRealm(t)?.twitter) }
                    }
                    MENTION ->

                    MentionTimeLine().apply {
                        val t = realm.where(AccountObject::class.java).equalTo("id", realmResults[i].accountId).findFirst()
                        arguments = Bundle { putByteArray("twitter", realm.copyFromRealm(t)?.twitter) }}


                    SEARCH -> SearchTimeline().apply {
                        arguments = Bundle {
                            putString("query_text", realmResults[i].searchWord)
                            putByteArray("query_bundle", realmResults[i].searchQuery)
                        }
                    }
                    LIST -> ListTimeLine().apply {
                        val t = realm.where(AccountObject::class.java).equalTo("id", realmResults[i].accountId).findFirst()
                        arguments = Bundle {
                            putLong("listId", realmResults[i].listId)
                            putByteArray("twitter", realm.copyFromRealm(t)?.twitter)
                        }
                    }
                    NOTIFICATION -> NotificationFragment()
                    TREND -> TrendFragment()
                    DM -> DMListFragment().apply {
                        val t = realm.where(AccountObject::class.java).equalTo("id", realmResults[i].accountId).findFirst()
                        arguments = Bundle { putByteArray("twitter", realm.copyFromRealm(t)?.twitter) }
                    }
                    SETTING -> RoselinFragment()
                    else -> throw IllegalStateException()}
        }
    }

    override fun getCount(): Int = realmResults.size


    override fun getPageTitle(position: Int): CharSequence =
            if (realmResults[position].screenName != null) "${toSimpleName(realmResults[position].type)}@${realmResults[position].screenName!!}"
            else toSimpleName(realmResults[position].type)
}
