package xyz.donot.roselin.view.activity

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
import xyz.donot.roselin.util.extraUtils.hideSoftKeyboard
import xyz.donot.roselin.util.extraUtils.intent
import xyz.donot.roselin.util.extraUtils.newIntent
import xyz.donot.roselin.util.extraUtils.start
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.util.haveToken
import xyz.donot.roselin.view.adapter.MainTimeLineAdapter




class MainActivity : AppCompatActivity() {
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
            if(!isActiveService()) {
         startService(Intent(this@MainActivity, StreamService ::class.java))
            }
            setUpHeader()
            setUpDrawerEvent()
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
