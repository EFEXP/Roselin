package xyz.donot.roselinx.view.activity

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user.*
import twitter4j.User
import xyz.donot.roselinx.R
import xyz.donot.roselinx.view.adapter.UserTimeLineAdapter
import xyz.donot.roselinx.viewmodel.UserViewModel
import kotlin.properties.Delegates


class UserActivity : AppCompatActivity(), LifecycleRegistryOwner {
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
        Picasso.with(applicationContext).load(user_.profileBannerIPadRetinaURL).into(banner)
        banner.setOnClickListener {
            startActivity(Intent(applicationContext, PictureActivity::class.java)
                    .putStringArrayListExtra("picture_urls", arrayListOf(user_.profileBannerIPadRetinaURL)))
        }
        toolbar.apply {
            title = user_.screenName
        }
        val adapter = UserTimeLineAdapter(supportFragmentManager)
        adapter.userId = user_.id
        viewpager_user.adapter = adapter
        viewpager_user.offscreenPageLimit = adapter.count
        tabs_user.setupWithViewPager(viewpager_user)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_user, menu)
        return true
    }


    private val life by lazy { LifecycleRegistry(this) }
    override fun getLifecycle(): LifecycleRegistry {
        return life
    }
}
