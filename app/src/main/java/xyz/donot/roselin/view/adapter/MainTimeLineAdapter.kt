package xyz.donot.roselin.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import xyz.donot.roselin.view.fragment.HomeTimeLineFragment


class MainTimeLineAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(i: Int): Fragment {
        return when (i) {
            0 -> {
                HomeTimeLineFragment()
            }
            1 -> {
                Fragment()
            }
            else -> {
                throw IllegalAccessError()
            }
        }

    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> {
                "Home"
            }
            1 -> {
                "Mention"
            }
            else -> {
                throw IllegalAccessError()
            }
        }
    }
}