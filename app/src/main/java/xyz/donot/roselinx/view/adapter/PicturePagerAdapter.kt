package xyz.donot.roselinx.view.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import xyz.donot.roselinx.view.custom.DynamicViewPager
import xyz.donot.roselinx.view.fragment.PictureFragment

class PicturePagerAdapter(fm: FragmentManager, private var pictureUrls: ArrayList<String>) : DynamicViewPager(fm) {

	override fun getItem(i: Int): Fragment {
		val p = PictureFragment()
		val bundle = Bundle()
		bundle.putString("url", pictureUrls[i])
		p.arguments = bundle
		return p
	}

	override fun getCount(): Int = pictureUrls.count()
	override fun getPageTitle(position: Int): CharSequence = position.toString()

}
