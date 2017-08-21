package xyz.donot.roselin.view.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import xyz.donot.roselin.view.custom.DynamicViewPager
import xyz.donot.roselin.view.fragment.PictureFragment

class PicturePagerAdapter(fm: FragmentManager, private var pictureUrls: ArrayList<String>) : DynamicViewPager(fm) {

    override fun getItem(i: Int): Fragment {
        val p = PictureFragment()
        val bundle = Bundle()
        bundle.putString("url", pictureUrls[i])
        p.arguments = bundle
        return p
    }
    override fun getCount(): Int {
        return pictureUrls.count()
    }
    override fun getPageTitle(position: Int): CharSequence {
        return position.toString()
    }

}
