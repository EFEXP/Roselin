package xyz.donot.roselinx.view.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.view.View
import android.widget.EditText
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_user.*
import twitter4j.User
import xyz.donot.roselinx.R
import xyz.donot.roselinx.view.adapter.UserTimeLineAdapter
import xyz.donot.roselinx.viewmodel.activity.UserViewModel
import kotlin.properties.Delegates


class UserActivity : AppCompatActivity(),Target {
    var viewmodel by Delegates.notNull<UserViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById<View>(android.R.id.content).systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        setContentView(R.layout.activity_user)
        viewmodel = ViewModelProviders.of(this).get(UserViewModel::class.java)
            if (intent.hasExtra("screen_name")) {
                viewmodel.initUser(intent.getStringExtra("screen_name"))
            } else {
                viewmodel.initUser(intent.getLongExtra("user_id", 0L))
            }
        viewmodel.mUser.observe(this, Observer {
            it?.let {
                setUp(it)
            }
        })
    }

    private fun setUp(user_: User) {
        Picasso.with(this).load(user_.profileBannerIPadRetinaURL).into(this)

        banner.setOnClickListener {
            startActivity(Intent(applicationContext, PictureActivity::class.java)
                    .putStringArrayListExtra("picture_urls", arrayListOf(user_.profileBannerIPadRetinaURL)))
        }
        toolbar.apply {
            title = user_.screenName
            setNavigationOnClickListener { onBackPressed() }
            inflateMenu(R.menu.menu_user)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.mute -> {
                        viewmodel.muteUser()
                    }
                    R.id.change_name -> {
                        val editText = EditText(this@UserActivity)
                        AlertDialog.Builder(this@UserActivity)
                                .setTitle("TLでの表示名を変更します")
                                .setView(editText)
                                .setPositiveButton("OK") { _, _ ->
                                    viewmodel.changeName(editText.text.toString())
                                }
                                .setNegativeButton("キャンセル") { _, _ -> }
                                .show()
                    }
                    R.id.revert_name -> {
                        viewmodel.revertName()
                    }
                    else -> onBackPressed()
                }
                return@setOnMenuItemClickListener true
            }
        }
        val adapter = UserTimeLineAdapter(supportFragmentManager)
        adapter.userId = user_.id
        viewpager_user.adapter = adapter
        viewpager_user.offscreenPageLimit = adapter.count
        tabs_user.setupWithViewPager(viewpager_user)
    }

    override fun onPrepareLoad(placeHolderDrawable: Drawable?) =Unit

    override fun onBitmapFailed(errorDrawable: Drawable?) =Unit

    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
        banner.setImageBitmap(bitmap)
        Palette.from(bitmap).generate(
                {
                    it.mutedSwatch?.let {
                        toolbar.setTitleTextColor(it.rgb)
                        banner.background=ColorDrawable(it.rgb)
                    }
                })
    }

}
