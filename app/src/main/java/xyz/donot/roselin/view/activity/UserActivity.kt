package xyz.donot.roselin.view.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.squareup.picasso.Picasso
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_user.*
import twitter4j.Twitter
import twitter4j.User
import xyz.donot.roselin.R
import xyz.donot.roselin.extend.SafeAsyncTask
import xyz.donot.roselin.model.realm.DBMute
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.util.getSerialized
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.view.adapter.UserTimeLineAdapter




class UserActivity : AppCompatActivity() {
    private  val userId: Long by lazy { intent.getLongExtra("user_id",0L) }
    private  val screenName: String by lazy { intent.getStringExtra("screen_name") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { findViewById<View>(android.R.id.content).systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE }
        setContentView(R.layout.activity_user)
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
         //   val w = window // in Activity's onCreate() for instance
         //   w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS) }

        class lookUpUserTask(private val userId:Long):SafeAsyncTask<Twitter,User>(){
            override fun onSuccess(result: User) = setUp(result)

            override fun onFailure(exception: Exception) = toast(exception.localizedMessage)

            override fun doTask(arg: Twitter): User = arg.showUser(userId)
        }
        class lookUpUserNameTask(private val screenName:String):SafeAsyncTask<Twitter,User>(){
            override fun onSuccess(result: User) = setUp(result)

            override fun onFailure(exception: Exception) = toast(exception.localizedMessage)

            override fun doTask(arg: Twitter): User = arg.showUser(screenName)
        }
        if(userId==0L) {
            lookUpUserNameTask(screenName).execute(getTwitterInstance())
        }
        else{  lookUpUserTask(userId).execute(getTwitterInstance())}
    }
    fun setUp(user_: User){
      Picasso.with(applicationContext).load(user_.profileBannerIPadRetinaURL).into(banner)
        banner.setOnClickListener{startActivity(Intent(applicationContext, PictureActivity::class.java)
                .putStringArrayListExtra("picture_urls",arrayListOf(user_.profileBannerIPadRetinaURL)))}
        toolbar.apply {
            title= user_.screenName
            inflateMenu(R.menu.menu_user)
            setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.mute-> {
                        Realm.getDefaultInstance().executeTransaction {
                            it.createObject(DBMute::class.java)
                                    .apply {
                                        id= user_.id
                                        user=user_.getSerialized()
                                    }
                        }
                    }
                    else->throw Exception()
                }
                true
            }
        }
        val adapter= UserTimeLineAdapter(supportFragmentManager)
        adapter.user= user_
        viewpager_user.adapter=adapter
        viewpager_user.offscreenPageLimit=adapter.count
        tabs_user.setupWithViewPager(viewpager_user)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }



}
