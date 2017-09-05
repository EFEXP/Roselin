package xyz.donot.roselin.view.activity

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.content_picture.*
import xyz.donot.roselin.R
import xyz.donot.roselin.view.adapter.PicturePagerAdapter


class PictureActivity : AppCompatActivity() {
	private val start by lazy { intent.extras.getInt("start_page", 0) }
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			findViewById<View>(android.R.id.content).systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
		}
		val strings = ArrayList<String>()
		setContentView(R.layout.activity_picture)
		if (intent.hasExtra("picture_urls"))
			strings.addAll(intent.extras.getStringArrayList("picture_urls"))
		val pager = picture_view_pager
		pager.offscreenPageLimit = strings.size
		val pagerAdapter = PicturePagerAdapter(supportFragmentManager, strings)
		pager.adapter = pagerAdapter
		pager.currentItem = start
	}

}
