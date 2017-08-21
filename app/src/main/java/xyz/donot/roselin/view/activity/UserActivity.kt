package xyz.donot.roselin.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user.*
import twitter4j.Twitter
import twitter4j.User
import xyz.donot.roselin.R
import xyz.donot.roselin.extend.SafeAsyncTask
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.view.adapter.UserTimeLineAdapter

class UserActivity : AppCompatActivity() {
    private  val userId: Long by lazy { intent.getLongExtra("user_id",0L) }
    private  val screenName: String by lazy { intent.getStringExtra("screen_name") }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        class lookUpUserTask(private val userId:Long):SafeAsyncTask<Twitter,User>(){
            override fun onSuccess(result: User) {
                setUp(result)
            }

            override fun onFailure(exception: Exception) {
                toast(exception.localizedMessage)
            }

            override fun doTask(arg: Twitter): User = arg.showUser(userId)
        }
        class lookUpUserNameTask(private val screenName:String):SafeAsyncTask<Twitter,User>(){
            override fun onSuccess(result: User) {
                setUp(result)
            }

            override fun onFailure(exception: Exception) {
                toast(exception.localizedMessage)
            }

            override fun doTask(arg: Twitter): User = arg.showUser(screenName)
        }
        if(userId==0L) {
            lookUpUserNameTask(screenName).execute(getTwitterInstance())
        }
        else{  lookUpUserTask(userId).execute(getTwitterInstance())}
    }
    fun setUp(user: User){
      Picasso.with(applicationContext).load(user.profileBannerIPadRetinaURL).into(banner)

        banner.setOnClickListener{startActivity(Intent(applicationContext, PictureActivity::class.java)
                .putStringArrayListExtra("picture_urls",arrayListOf(user.profileBannerIPadRetinaURL)))}
        toolbar.title=user.screenName
        //toolbar.subtitle=user.screenName
        val adapter= UserTimeLineAdapter(supportFragmentManager)
        adapter.user=user
        viewpager_user.adapter=adapter
        viewpager_user.offscreenPageLimit=1
        tabs_user.setupWithViewPager(viewpager_user)


        }




}
