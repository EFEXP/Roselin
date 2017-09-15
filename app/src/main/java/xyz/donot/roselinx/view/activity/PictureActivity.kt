package xyz.donot.roselinx.view.activity

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_picture.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.hide
import xyz.donot.roselinx.view.adapter.PicturePagerAdapter
import xyz.donot.roselinx.viewmodel.PictureViewModel
import xyz.klinker.android.drag_dismiss.activity.DragDismissActivity


class PictureActivity : DragDismissActivity(),LifecycleRegistryOwner {
    private val start by lazy { intent.extras.getInt("start_page", 0) }
   lateinit private var viewmodel:PictureViewModel
    override fun onCreateContent(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View =inflater.inflate(R.layout.activity_picture,parent,false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           findViewById<View>(android.R.id.content).systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

        delegate.toolbar.hide()
       viewmodel= ViewModelProviders.of(this).get(PictureViewModel::class.java)
        val strings = ArrayList<String>()
        if (intent.hasExtra("picture_urls"))
            strings.addAll(intent.extras.getStringArrayList("picture_urls"))
        picture_view_pager.apply {
            val pagerAdapter = PicturePagerAdapter(supportFragmentManager, strings)
            offscreenPageLimit = strings.size
            adapter = pagerAdapter
            currentItem = start
        }
        viewmodel.mutedColor.observe(this, Observer {
           it?.let {
               delegate.toolbar.background= ColorDrawable(Color.TRANSPARENT)
           }
        })
    }
    private val life by lazy { LifecycleRegistry(this) }
    override fun getLifecycle(): LifecycleRegistry {
        return life
    }

}
