package xyz.donot.roselin.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_header.*
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.User
import xyz.donot.quetzal.view.fragment.getMyId
import xyz.donot.roselin.R
import xyz.donot.roselin.extend.SafeAsyncTask
import xyz.donot.roselin.service.StreamService
import xyz.donot.roselin.util.extraUtils.*
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.util.haveToken
import xyz.donot.roselin.view.adapter.MainTimeLineAdapter


class MainActivity : AppCompatActivity() {
   private val REQUEST_WRITE_READ=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        if (!haveToken()) {
            startActivity(intent<OauthActivity>())
            this.finish()
        }
        else{
           toolbar.inflateMenu(R.menu.menu_main)
            button_tweet.setOnClickListener {
                if (!editText_status.text.isNullOrBlank() && editText_status.text.count() <= 140){
                    class SendTask(val txt:String): SafeAsyncTask<Twitter, Status>(){
                        override fun doTask(arg: Twitter): twitter4j.Status = arg.updateStatus(txt)

                        override fun onSuccess(result: twitter4j.Status) {
                            editText_status.hideSoftKeyboard()
                            editText_status.setText("")
                        }
                        override fun onFailure(exception: Exception) {
                            editText_status.hideSoftKeyboard()
                        }
                    }
                    SendTask(editText_status.editableText.toString()).execute(getTwitterInstance())

                }

            }
            val adapter=MainTimeLineAdapter(supportFragmentManager)
           main_viewpager.adapter = adapter
           main_viewpager.offscreenPageLimit = 2
            //stream
            if(!isActiveService()) {
         startService(Intent(this@MainActivity, StreamService ::class.java))
            }
            //view
            fab.setOnClickListener{start<TweetEditActivity>()}
            if (!defaultSharedPreferences.getBoolean("quick_tweet",true)){editText_layout.visibility= View.GONE}
            setUpHeader()
            setUpDrawerEvent()
            setUpView()
            InitialRequestPermission()
         }


}

    private fun setUpView() {
        val uriString=defaultSharedPreferences.getString("BackGroundUri","")
        if (!uriString.isNullOrBlank()){
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,   Uri.parse(uriString))
            main_coordinator.background=BitmapDrawable(resources, bitmap)
        }
    }

    @SuppressLint("NewApi")
    private fun InitialRequestPermission() {
        fromApi(23, true){
            val EX_WRITE= ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
            val LOCATION=ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
            val EX_READ=ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
            if(!(EX_WRITE&&EX_READ&&LOCATION)){
                requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ,Manifest.permission.READ_EXTERNAL_STORAGE
                                ,Manifest.permission.ACCESS_FINE_LOCATION)
                        ,REQUEST_WRITE_READ)
            }
        }
    }


    private fun isActiveService(): Boolean {
        val activityManager =getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServicesInfo = activityManager.getRunningServices(Integer.MAX_VALUE)
       // logd("DataReceiver",isActive.toString())
        return runningServicesInfo.any { it.service.className == StreamService ::class.java.name.toString()}

    }

    private fun setUpDrawerEvent() {
        navigation_drawer.setNavigationItemSelectedListener({
                when (it.itemId) {
                    R.id.my_profile -> {
                       startActivity(newIntent<UserActivity>(Bundle().apply { putLong("user_id", getMyId()) }))
                        drawer_layout.closeDrawers()
                    }
                    R.id.action_help -> {
                      //  HelpFragment().show(supportFragmentManager,"")
                        drawer_layout.closeDrawers()
                    }
                    R.id.action_setting -> {
                        start<SettingsActivity>()
                        drawer_layout.closeDrawers()
                    }
                    R.id.action_account -> {
                  //      startForResult<AccountSettingActivity>(0)
                        drawer_layout.closeDrawers()
                    }
                    R.id.action_list -> {
                   //     start<ListsActivity>(Bundle().apply { putLong("user_id",getMyId()) })
                        drawer_layout.closeDrawers()
                    }
                    R.id.action_whats_new -> {
                    //    onCustomTabEvent("http://donot.xyz/")
                        drawer_layout.closeDrawers()
                    }
                }
            true
        })

    }


    private fun setUpHeader(){
        class HeaderTask:SafeAsyncTask<Twitter,User>(){
            override fun doTask(arg: Twitter): User {
                return arg.verifyCredentials()
            }

            override fun onSuccess(result: User) {
                Picasso.with(applicationContext).load(result.profileBannerIPadRetinaURL).into(my_header)
                Picasso.with(applicationContext).load(result.originalProfileImageURLHttps).into(my_icon)
                my_name_header.text= result.name
                my_screen_name_header.text = "@${result.screenName}"
            }

            override fun onFailure(exception: Exception) {

            }
        }
        HeaderTask().execute(getTwitterInstance())
    }







}
