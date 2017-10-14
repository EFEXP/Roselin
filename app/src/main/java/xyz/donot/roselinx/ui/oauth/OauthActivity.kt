package xyz.donot.roselinx.ui.oauth

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.android.synthetic.main.content_oauth.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.ui.main.MainActivity
import xyz.donot.roselinx.ui.util.Key.xxxxx
import xyz.donot.roselinx.ui.util.Key.yyyyyy
import xyz.donot.roselinx.ui.util.extraUtils.hide
import xyz.donot.roselinx.ui.util.extraUtils.show
import xyz.donot.roselinx.ui.util.extraUtils.start
import xyz.donot.roselinx.ui.util.extraUtils.toast


class OauthActivity : AppCompatActivity() {
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        login_button.onActivityResult(requestCode, resultCode, data)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val anim = AlphaAnimation(0.0f, 1.0f).apply {
            duration = 500
            startOffset = 20
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        setContentView(R.layout.activity_oauth)
        val viewmodel = ViewModelProviders.of(this).get(OauthViewModel::class.java)
        viewmodel.isFinished.observe(this, Observer {
          val  flags =Intent.FLAG_ACTIVITY_CLEAR_TASK
            this.start<MainActivity>(flags)
            finish()
        })
        login_button.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                login_button.hide()
                progressBar.show()
                tv_information.startAnimation(anim)
                viewmodel.onSuccess(yyyyyy,xxxxx, result)
            }
            override fun failure(exception: TwitterException?) = toast("失敗しました。")
        }
        viewmodel.information.observe(this, Observer {
            it?.let {
                tv_information.text = it
            }
        })



    }

}
