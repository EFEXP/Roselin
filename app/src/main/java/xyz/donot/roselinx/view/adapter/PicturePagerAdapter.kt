package xyz.donot.roselinx.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import xyz.donot.roselinx.util.extraUtils.Bundle
import xyz.donot.roselinx.view.custom.DynamicViewPager
import xyz.donot.roselinx.view.fragment.PictureFragment

class PicturePagerAdapter(fm: FragmentManager, private var pictureUrls: ArrayList<String>) : DynamicViewPager(fm) {

    override fun getItem(i: Int): Fragment = PictureFragment()
            .apply {
                arguments = Bundle {
                    putString("url", pictureUrls[i])
                }
            }
    override fun getCount(): Int = pictureUrls.count()
    override fun getPageTitle(position: Int): CharSequence = position.toString()

}
