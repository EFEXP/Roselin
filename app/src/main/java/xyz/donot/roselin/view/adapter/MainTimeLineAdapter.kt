package xyz.donot.roselin.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import xyz.donot.roselin.view.fragment.status.HomeTimeLineFragment
import xyz.donot.roselin.view.fragment.status.MentionTimeLine
import xyz.donot.roselin.view.fragment.status.NotificationFragment


class MainTimeLineAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(i: Int): Fragment = when (i) {
        0 -> {
            HomeTimeLineFragment()
        }
        1 -> {
           MentionTimeLine()
        }
        2->{
            NotificationFragment()
        }
        else -> {
            throw IllegalAccessError()
        }
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> {
                "Home"
            }
            1 -> {
                "Mention"
            }
            3 -> {
                "Notification"
            }
            else -> {
                throw IllegalAccessError()
            }
        }
    }
}