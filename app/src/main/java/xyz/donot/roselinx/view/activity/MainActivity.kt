package xyz.donot.roselinx.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.squareup.picasso.Picasso
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_header.view.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.realm.DBTabData
import xyz.donot.roselinx.util.extraUtils.*
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.util.haveToken
import xyz.donot.roselinx.view.adapter.MainTimeLineAdapter
import xyz.donot.roselinx.viewmodel.MainViewModel
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity(), LifecycleRegistryOwner {
    private var viewmodel by Delegates.notNull<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!haveToken()) {
            startActivity(intent<OauthActivity>())
            this.finish()
        } else if (isConnected()) {
            viewmodel = ViewModelProviders.of(this).get(MainViewModel::class.java)
            viewmodel.registerReceivers()
            viewmodel.initTab()
            viewmodel.isConnectedStream.observe(this, Observer {
                it?.let {
                    mainThread {
                            iv_connected_stream.setImageDrawable(ResourcesCompat.getDrawable(resources, if (it)R.drawable.ic_cloud else R.drawable.ic_cloud_off, null))
                    }
                }
            })
            toolbar.apply {
                title = context.getString(R.string.title_home)
                inflateMenu(R.menu.menu_main)
                setNavigationOnClickListener { drawer_layout.openDrawer(GravityCompat.START) }
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_search -> start<SearchSettingActivity>()
                        else -> throw Exception()
                    }
                    true
                }
            }
            // stream&savedInstance
                /*if (defaultSharedPreferences.getBoolean("use_search_stream", false)) {
                    val result = realm.where(DBTabData::class.java).equalTo("type", SEARCH).findAll()
                    result.forEach {
                        startService(newIntent<SearchStreamService>(Bundle {
                            putString("query_text", it.searchWord)
                        }))
                    }
                }*/
           if (savedInstanceState==null) viewmodel.initStream()
            if (!defaultSharedPreferences.getBoolean("quick_tweet", false)) {
                editText_layout.visibility = View.GONE
            }
            viewmodel.user.observe(this, Observer {
                it?.let {
                    user->
                    navigation_drawer.getHeaderView(0).also {
                        Picasso.with(applicationContext).load(user.profileBannerIPadRetinaURL).into(it.my_header)
                        Picasso.with(applicationContext).load(user.originalProfileImageURLHttps).into(it.my_icon)
                        it.my_name_header.text = user.name
                        it.my_screen_name_header.text = "@${user.screenName}"
                    }
                }
            })
            viewmodel.initUser()
            setUpDrawerEvent()
            setUpView()
            InitialRequestPermission()
        }
    }

    private fun setUpDrawerEvent() = navigation_drawer.setNavigationItemSelectedListener({
        when (it.itemId) {
            R.id.my_profile -> {
                start<UserActivity>(Bundle().apply { putLong("user_id", getMyId()) })
                drawer_layout.closeDrawers()
            }
            R.id.action_help -> {
                start<HelpActivity>()
                //  HelpFragment().show(supportFragmentManager,"")
                drawer_layout.closeDrawers()
            }
            R.id.action_setting -> {
                start<SettingsActivity>()
                drawer_layout.closeDrawers()
            }
            R.id.action_account -> {
                startForResult<AccountSettingActivity>(0)
                drawer_layout.closeDrawers()
            }

        }
        drawer_layout.isSelected = false
        true
    })

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

    }

    private fun setUpView() {
        //pager
        Realm.getDefaultInstance().use {
            val list = it.copyFromRealm(it.where(DBTabData::class.java).findAll()).toList()
            val adapter = MainTimeLineAdapter(supportFragmentManager, list)
            main_viewpager.adapter = adapter
            main_viewpager.offscreenPageLimit = adapter.count
        }
        viewmodel.postSucceed.observe(this, Observer {
            editText_status.hideSoftKeyboard()
            it?.let {status->
                editText_status.editableText.clear()
                Snackbar.make(window.decorView.rootView,"投稿しました", Snackbar.LENGTH_LONG).setAction("取り消し",{viewmodel.deleteTweet(status.id)}).show()
            }
        })
        viewmodel.deleteSucceed.observe(this, Observer { toast("削除しました") })

        val uriString = defaultSharedPreferences.getString("BackGroundUri", "")
        if (!uriString.isNullOrBlank()) {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(uriString))
            main_coordinator.background = BitmapDrawable(resources, bitmap)
            background_overlay.show()
        }
        if (defaultSharedPreferences.getBoolean("use_home_tab", false)) {
            tabs_main.setupWithViewPager(main_viewpager)
        } else {
            tabs_main.hide()
        }
        //view
        fab.setOnClickListener { start<TweetEditActivity>() }
        button_tweet.setOnClickListener {
            viewmodel.sendTweet(text = editText_status.editableText.toString())
        }
    }
    //Permission
    private val REQUEST_WRITE_READ = 0
    @SuppressLint("NewApi")
    private fun InitialRequestPermission() = fromApi(23) {
        val EX_WRITE = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val LOCATION = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val EX_READ = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        if (!(EX_WRITE && EX_READ && LOCATION)) {
            requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.ACCESS_FINE_LOCATION)
                    , REQUEST_WRITE_READ)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                restart()
            }
        }
    }

    private val life by lazy { LifecycleRegistry(this) }
    override fun getLifecycle(): LifecycleRegistry {
        return  life
    }

}
