package xyz.donot.roselinx.view.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_picture.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.hide
import xyz.donot.roselinx.util.extraUtils.logd
import xyz.donot.roselinx.util.extraUtils.onClick
import xyz.donot.roselinx.view.adapter.PicturePagerAdapter
import xyz.donot.roselinx.viewmodel.fragment.PictureViewModel
import xyz.klinker.android.drag_dismiss.activity.DragDismissActivity


class PictureActivity : DragDismissActivity() {
    private val start by lazy { intent.extras.getInt("start_page", 0) }
    lateinit private var viewmodel: PictureViewModel
    override fun onCreateContent(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.activity_picture, parent, false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById<View>(android.R.id.content).systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        delegate.toolbar.hide()
        viewmodel = ViewModelProviders.of(this).get(PictureViewModel::class.java)
        if (intent.hasExtra("picture_urls")) {
            val list = intent.extras.getStringArrayList("picture_urls")
            logd { "has Url" + list.size + "ex:" + list[0] }
            viewmodel.urlList.value = list
        } else throw IllegalArgumentException()
        viewmodel.urlList.observe(this, Observer {
            it?.let {
                picture_view_pager.apply {
                    val pagerAdapter = PicturePagerAdapter(supportFragmentManager, it)
                    offscreenPageLimit = it.size
                    adapter = pagerAdapter
                    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                        override fun onPageScrollStateChanged(state: Int) {
                        }

                        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                        }

                        override fun onPageSelected(position: Int) {
                            viewmodel.currentPage = position
                        }

                    })
                    currentItem = start
                }
            }
        })

        viewmodel.mutedColor.observe(this, Observer {
            it?.let {
                val drawable = DrawableCompat.wrap(ContextCompat.getDrawable(this, R.drawable.ic_file_download))
                DrawableCompat.setTint(drawable, it.bodyTextColor)
                logd { it.bodyTextColor }
                DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
                bt_download.setImageDrawable(drawable)
                picture_background.background = ColorDrawable(it.rgb)
            }
        })
        bt_download.onClick {
            viewmodel.savePicture()
        }
    }


}
