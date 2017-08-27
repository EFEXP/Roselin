package xyz.donot.roselin.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.User
import xyz.donot.roselin.R
import xyz.donot.roselin.extend.SafeAsyncTask
import xyz.donot.roselin.model.realm.*
import xyz.donot.roselin.service.SearchStreamService
import xyz.donot.roselin.service.StreamingService
import xyz.donot.roselin.util.extraUtils.*
import xyz.donot.roselin.util.getMyId
import xyz.donot.roselin.util.getMyScreenName
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.util.haveToken
import xyz.donot.roselin.view.adapter.MainTimeLineAdapter


class MainActivity : AppCompatActivity() {
   private val REQUEST_WRITE_READ=0
   private var user:User?=null
    private val disConnectionReceiver by lazy { DisConnectionReceiver() }
    private val connectionReceiver by lazy { ConnectionReceiver() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val realm =  Realm.getDefaultInstance()
        setContentView(R.layout.activity_main)
        if (!haveToken()) {
            startActivity(intent<OauthActivity>())
            this.finish()
        }
        else{
            //Receiver
            LocalBroadcastManager.getInstance(this).apply {
                registerReceiver(disConnectionReceiver, IntentFilter("OnDisconnect"))
                registerReceiver(connectionReceiver, IntentFilter("OnConnect"))
            }
            //initial tabdata
            if (realm.where(DBTabData::class.java).count()==0L){
                realm.executeTransaction {
                    it.createObject(DBTabData::class.java).apply {
                        order=0
                        type= HOME
                        accountId= getMyId()
                        screenName= getMyScreenName()
                    }
                    it.createObject(DBTabData::class.java).apply {
                        order=1
                        type= NOTIFICATION
                        accountId= getMyId()
                        screenName= getMyScreenName()
                    }
                    it.createObject(DBTabData::class.java).apply {
                        order=2
                        type= MENTION
                        accountId= getMyId()
                        screenName= getMyScreenName()
                    }
                }
            }

            toolbar.apply {
                title = context.getString(R.string.title_home)
                inflateMenu(R.menu.menu_main)
                setNavigationOnClickListener { drawer_layout.openDrawer(GravityCompat.START) }
                setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.menu_search-> start<SearchSettingActivity>()
                        else->throw Exception()
                    }
                    true
                   }
            }
            // stream&savedInstance
            if(savedInstanceState==null) {
         if (defaultSharedPreferences.getBoolean("use_home_stream",true)){

               startService<StreamingService>()
           }
                if (defaultSharedPreferences.getBoolean("use_search_stream",false)){
                val result=   realm.where(DBTabData::class.java).equalTo("type", SEARCH).findAll()
                result.forEach {
                startService(newIntent<SearchStreamService>(Bundle {
                    putString("query_text",it.searchWord)
                    }))
                }}
            }
            else{
                user= savedInstanceState.getSerializable("user") as User
            }
            if (!defaultSharedPreferences.getBoolean("quick_tweet",false)){ editText_layout.visibility= View.GONE}
            setUpHeader()
            setUpDrawerEvent()
            setUpView()
            InitialRequestPermission()
         }




}

    @SuppressLint("NewApi")
    private fun InitialRequestPermission() = fromApi(23){
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

    private fun setUpDrawerEvent() = navigation_drawer.setNavigationItemSelectedListener({
            when (it.itemId) {
                R.id.my_profile -> {
                    start<UserActivity>(Bundle().apply { putLong("user_id", getMyId()) })
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
                    startForResult<AccountSettingActivity>(0)
                    drawer_layout.closeDrawers()
                }
                R.id.action_whats_new -> {
                //    onCustomTabEvent("http://donot.xyz/")
                    drawer_layout.closeDrawers()
                }
            }
        true
    })


    private fun setUpHeader(){
        val view = navigation_drawer.getHeaderView(0)
        val my_banner=view.findViewById<ImageView>(R.id.my_header)
        val my_profile=view.findViewById<ImageView>(R.id.my_icon)
        val my_name=view.findViewById<TextView>(R.id.my_name_header)
        val my_screenname=view.findViewById<TextView>(R.id.my_screen_name_header)
        if (user==null){ async { user= getTwitterInstance().verifyCredentials()
         mainThread {
             Picasso.with(applicationContext).load(user?.profileBannerIPadRetinaURL).into(my_banner)
             Picasso.with(applicationContext).load(user?.originalProfileImageURLHttps).into(my_profile)
             my_name.text= user?.name
             my_screenname.text = "@${user?.screenName}"
         }
        }
        }
        else{
            Picasso.with(applicationContext).load(user?.profileBannerIPadRetinaURL).into(my_banner)
            Picasso.with(applicationContext).load(user?.originalProfileImageURLHttps).into(my_profile)
            my_name.text= user?.name
            my_screenname.text = "@${user?.screenName}" }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (user!=null){
        outState.putSerializable("user",user)
    }

    }

    private fun setUpView() {
        //pager
        val realm =  Realm.getDefaultInstance()
        val list= ArrayList<DBTabData>().apply {
            realm.where(DBTabData::class.java)
                    .findAll().forEach {
                add(realm.copyFromRealm(it))
            }
        }
        val adapter= MainTimeLineAdapter(supportFragmentManager,list)
        main_viewpager.adapter = adapter
        main_viewpager.offscreenPageLimit = adapter.count
        val uriString=defaultSharedPreferences.getString("BackGroundUri","")
        if (!uriString.isNullOrBlank()){
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,Uri.parse(uriString))
            main_coordinator.background=BitmapDrawable(resources, bitmap)
            background_overlay.show()
        }
        if (defaultSharedPreferences.getBoolean("use_home_tab",false)){tabs_main.setupWithViewPager(main_viewpager)}
        //view
        fab.setOnClickListener{start<TweetEditActivity>()}
        button_tweet.setOnClickListener {
            if (!editText_status.text.isNullOrBlank() && editText_status.text.count() <= 140){
                class SendTask(val txt:String): SafeAsyncTask<Twitter, Status>(){
                    override fun doTask(arg: Twitter): twitter4j.Status = arg.updateStatus(txt)

                    override fun onSuccess(result: twitter4j.Status) {
                        editText_status.hideSoftKeyboard()
                        editText_status.setText("")
                    }
                    override fun onFailure(exception: Exception) = editText_status.hideSoftKeyboard()
                }
                SendTask(editText_status.editableText.toString()).execute(getTwitterInstance())
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==0){
                restart()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        stopService(newIntent<StreamingService>())
        LocalBroadcastManager.getInstance(this).apply {
            unregisterReceiver(connectionReceiver)
            unregisterReceiver(disConnectionReceiver)
        }
    }
    inner class ConnectionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
         mainThread {   iv_connected_stream.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_cloud,null))  }
        }
    }
    inner class DisConnectionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mainThread {   iv_connected_stream.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_cloud_off,null))}
        }
    }

}
