package xyz.donot.roselin.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import xyz.donot.roselin.view.fragment.HomeTimeLineFragment


class MainTimeLineAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(i: Int): Fragment {
        when (i) {
            0 -> {
                return HomeTimeLineFragment()
            }
            1 -> {
                return Fragment()
            }
            else -> {
                throw IllegalAccessError()
            }
        }

    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> {
                return "Home"
            }
            1 -> {
                return "Mention"
            }
            else -> {
                throw IllegalAccessError()
            }
        }
    }
}