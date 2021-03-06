package xyz.donot.roselinx.ui.detailuser

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter


class UserTimeLineAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    var userId: Long = 0
    override fun getItem(position: Int): Fragment = when (position) {
        0 -> UserTimeLineFragment.newInstance(userId)
        1 -> FavoriteTimeLine.newInstance(userId)
        else -> throw  IllegalStateException()
    }
    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "Info"
        1 -> "Favorite"
        else -> throw IllegalStateException()
    }

    override fun getCount(): Int = 2


}
