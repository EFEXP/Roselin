package xyz.donot.roselinx.ui.picture

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import xyz.donot.roselinx.ui.util.extraUtils.bundle
import xyz.donot.roselinx.ui.view.DynamicViewPager

class PicturePagerAdapter(fm: FragmentManager, private var pictureUrls: ArrayList<String>) : DynamicViewPager(fm) {

    override fun getItem(i: Int): Fragment = PictureFragment().apply { arguments = bundle { putInt("page",i) } }
    override fun getCount(): Int = pictureUrls.count()
    override fun getPageTitle(position: Int): CharSequence = position.toString()

}
