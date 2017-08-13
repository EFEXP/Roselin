package xyz.donot.roselin.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.content_picture.*
import xyz.donot.roselin.R
import xyz.donot.roselin.view.adapter.PicturePagerAdapter

class PictureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)
        val strings = intent.extras.getStringArrayList("picture_urls")
        val pager=picture_view_pager
        pager.offscreenPageLimit=strings.count()
        val pagerAdapter= PicturePagerAdapter(supportFragmentManager,strings)
        pager.adapter = pagerAdapter
    }

}
