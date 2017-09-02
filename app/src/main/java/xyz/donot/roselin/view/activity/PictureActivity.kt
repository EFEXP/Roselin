package xyz.donot.roselin.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.content_picture.*
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.fromApi
import xyz.donot.roselin.view.adapter.PicturePagerAdapter

class PictureActivity : AppCompatActivity() {
	private val strings by lazy { intent.extras.getStringArrayList("picture_urls") }
	private val start by lazy { intent.extras.getInt("start_page", 0) }
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_picture)
		val pager = picture_view_pager
		pager.offscreenPageLimit = strings.size
		val pagerAdapter = PicturePagerAdapter(supportFragmentManager, strings)
		pager.adapter = pagerAdapter
		pager.currentItem = start
	}


	@SuppressLint("InlinedApi")
	override fun onWindowFocusChanged(hasFocus: Boolean) {
		super.onWindowFocusChanged(hasFocus)
		if (hasFocus) {
			fromApi(19) {
				window.decorView.systemUiVisibility =
						View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
								View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
								View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
								View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
								View.SYSTEM_UI_FLAG_FULLSCREEN or
								View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
			}
		}
	}

}
