package xyz.donot.roselin.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import twitter4j.Status
import twitter4j.Twitter
import xyz.donot.roselin.R
import xyz.donot.roselin.extend.SafeAsyncTask
import xyz.donot.roselin.util.extraUtils.hideSoftKeyboard
import xyz.donot.roselin.util.extraUtils.intent
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.util.haveToken
import xyz.donot.roselin.view.fragment.HomeTimeLineFragment


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
            val fragment = HomeTimeLineFragment()
            supportFragmentManager.beginTransaction().add(R.id.container, fragment).commit()
            button_tweet.setOnClickListener {
                if (!editText_status.text.isNullOrBlank() && editText_status.text.count() <= 140){
                    class SendTask(val txt:String): SafeAsyncTask<Twitter, Status>(){
                        override fun doTask(arg: Twitter): twitter4j.Status {
                            return  arg.updateStatus(txt)
                        }

                        override fun onSuccess(result: twitter4j.Status) {
                            editText_status.hideSoftKeyboard()
                            editText_status.setText("")
                        }

                        override fun onFailure(exception: Exception) {

                        }
                    }
                    SendTask(editText_status.editableText.toString()).execute(getTwitterInstance())

                }

            }

         }

}










}
