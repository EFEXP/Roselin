package xyz.donot.roselinx.view.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.android.synthetic.main.content_oauth.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.Key.xxxxx
import xyz.donot.roselinx.util.Key.yyyyyy
import xyz.donot.roselinx.util.extraUtils.hide
import xyz.donot.roselinx.util.extraUtils.show
import xyz.donot.roselinx.util.extraUtils.start
import xyz.donot.roselinx.util.extraUtils.toast
import xyz.donot.roselinx.viewmodel.OauthViewModel


class OauthActivity : AppCompatActivity() {
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        login_button.onActivityResult(requestCode, resultCode, data)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oauth)
        val viewmodel = ViewModelProviders.of(this).get(OauthViewModel::class.java)
        viewmodel.isFinished.observe(this, Observer {
          val  flags =Intent.FLAG_ACTIVITY_CLEAR_TASK
            this.start<MainActivity>(flags)
            finish()
        })
        viewmodel.information.observe(this, Observer {
          it?.let {
              tv_information.text = it
          }
        })
        login_button.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                //getString(R.string.twitter_official_consumer_key) getString(R.string.twitter_official_consumer_secret)
                login_button.hide()
                progressBar.show()
                viewmodel.onSuccess(yyyyyy,xxxxx, result)
            }
            override fun failure(exception: TwitterException?) = toast("失敗しました。")
        }

    }

}
