package xyz.donot.roselinx.view.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import xyz.donot.roselinx.view.fragment.status.FavoriteTimeLine
import xyz.donot.roselinx.view.fragment.status.UserTimeLineFragment


class UserTimeLineAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    var userId: Long = 0
    override fun getItem(position: Int): Fragment = when (position) {
        0 -> {
            val fragment = UserTimeLineFragment()
            val bundle = Bundle()
            bundle.putLong("userId", userId)
            fragment.arguments = bundle
            fragment
        }
        1 -> {
            val fragment = FavoriteTimeLine()
            val bundle = Bundle()
            bundle.putLong("userId", userId)
            fragment.arguments = bundle
            fragment
        }

        else -> throw  IllegalStateException()
    }


    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "Info"
        1 -> "Favorite"
        else -> throw IllegalStateException()
    }

    override fun getCount(): Int = 2


}
