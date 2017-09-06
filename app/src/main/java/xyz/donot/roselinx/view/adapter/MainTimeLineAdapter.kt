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


class MainTimeLineAdapter(fm: FragmentManager, private val realmResults: List<DBTabData>) : FragmentPagerAdapter(fm) {
	private val realm = Realm.getDefaultInstance()
	override fun getItem(i: Int): Fragment = when (realmResults[i].type) {
		HOME -> HomeTimeLineFragment().apply {
			val t = realm.where(DBAccount::class.java).equalTo("id", realmResults[i].accountId).findFirst()
			arguments = Bundle { putByteArray("twitter", realm.copyFromRealm(t)?.twitter) }
		}
		MENTION -> MentionTimeLine().apply {
			val t = realm.where(DBAccount::class.java).equalTo("id", realmResults[i].accountId).findFirst()
			arguments = Bundle { putByteArray("twitter", realm.copyFromRealm(t)?.twitter) }
		}
		SEARCH -> SearchTimeline().apply {
			arguments = Bundle {
				putString("query_text", realmResults[i].searchWord)
				putByteArray("query_bundle", realmResults[i].searchQuery)
			}
		}
		LIST -> ListTimeLine().apply {
			val t = realm.where(DBAccount::class.java).equalTo("id", realmResults[i].accountId).findFirst()
			arguments = Bundle {
				putLong("listId", realmResults[i].listId)
				putByteArray("twitter", realm.copyFromRealm(t)?.twitter) }
		}
		NOTIFICATION -> NotificationFragment()
		TREND -> TrendFragment()
		DM -> DMListFragment().apply {
			val t = realm.where(DBAccount::class.java).equalTo("id", realmResults[i].accountId).findFirst()
			arguments = Bundle { putByteArray("twitter", realm.copyFromRealm(t)?.twitter) }
		}
		else -> throw IllegalStateException()
	}

	override fun getCount(): Int = realmResults.size


	override fun getPageTitle(position: Int): CharSequence =
			if (realmResults[position].screenName != null) "${ConvertToSimpleName(realmResults[position].type)}@${realmResults[position].screenName!!}"
			else ConvertToSimpleName(realmResults[position].type)
}