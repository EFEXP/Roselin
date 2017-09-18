package xyz.donot.roselinx.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.error_activity.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.realm.DBTabData
import xyz.donot.roselinx.util.extraUtils.*
import xyz.donot.roselinx.util.haveToken
import xyz.donot.roselinx.view.adapter.MainTimeLineAdapter
import xyz.donot.roselinx.viewmodel.MainViewModel


class MainActivity : AppCompatActivity() {
    private val viewmodel: MainViewModel by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!haveToken()) {
            startActivity(intent<OauthActivity>())
            this.finish()
        } else if (isConnected()) {
            setUp(savedInstanceState)
        } else {
            setContentView(R.layout.error_activity)
            button_retry.onClick = {
                if (isConnected()) {
                    setUp(savedInstanceState)
                }
            }
        }
    }

    private fun setUp(bundle: Bundle?) {
        setContentView(R.layout.activity_main)
        viewmodel.initTab()
        setUpView()
        viewmodel.apply {
            registerReceivers()
            initUser()
            if (bundle == null) {
                initStream()
            }
        }
        initialRequestPermission()
    }

    @SuppressLint("NewApi")
    private fun setUpView() {
        if (!defaultSharedPreferences.getBoolean("quick_tweet", false)) {
            editText_layout.visibility = View.GONE
        }

        //pager
        Realm.getDefaultInstance().use {
            val list = it.copyFromRealm(it.where(DBTabData::class.java).findAll()).toList()
            val adapter = MainTimeLineAdapter(supportFragmentManager, list)
            main_viewpager.adapter = adapter
            main_viewpager.offscreenPageLimit = adapter.count
            main_viewpager.currentItem = 1

            viewmodel.postSucceed.observe(this, Observer {
                editText_status.hideSoftKeyboard()
                it?.let { status ->
                    editText_status.editableText.clear()
                    Snackbar.make(main_coordinator, "投稿しました", Snackbar.LENGTH_SHORT).setAction("取り消し", { viewmodel.deleteTweet(status.id) }).show()
                }
            })
            viewmodel.deleteSucceed.observe(this, Observer { Snackbar.make(main_coordinator, "削除しました", Snackbar.LENGTH_SHORT).show() })

            val uriString = defaultSharedPreferences.getString("BackGroundUri", "")
            if (!uriString.isEmpty()) {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(uriString))
                val bitmapDrawable = BitmapDrawable(resources, bitmap)
                val porter = if (defaultSharedPreferences.getBoolean("night", true)) PorterDuff.Mode.DARKEN else PorterDuff.Mode.LIGHTEN
                val d2 = DrawableCompat.wrap(bitmapDrawable)
                DrawableCompat.setTint(d2, ContextCompat.getColor(this@MainActivity, R.color.overlay_background))
                DrawableCompat.setTintMode(d2, porter)
                main_coordinator.background = d2
            }
            tabs_main.setupWithViewPager(main_viewpager)
            fab.setOnClickListener { start<EditTweetActivity>() }
            button_tweet.setOnClickListener {
                viewmodel.sendTweet(text = editText_status.editableText.toString())
            }
        }
    }

    //Permission
    private val REQUEST_WRITE_READ = 0

    @SuppressLint("NewApi")
    private fun initialRequestPermission() = fromApi(23, true) {
        val hasPermission = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        if (hasPermission.not()) {
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


}

