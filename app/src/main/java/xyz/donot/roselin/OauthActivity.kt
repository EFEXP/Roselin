package xyz.donot.roselin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.crashlytics.android.answers.LoginEvent
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import io.realm.Realm
import kotlinx.android.synthetic.main.content_oauth.*
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import xyz.donot.roselin.model.realm.DBAccount
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.util.getSerialized






class OauthActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oauth)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

      login_button.callback= object : Callback<TwitterSession>() {
          override fun success(result: Result<TwitterSession>) {
              val factory = TwitterFactory()
              val accessToken =AccessToken(result.data.authToken.token,result.data.authToken.secret)
              val twitter = factory.instance
              twitter.setOAuthConsumer(getString(R.string.twitter_consumer_key),getString(R.string.twitter_consumer_secret))
              twitter.oAuthAccessToken = accessToken
              saveToken(tw =twitter)
              logUser(tw =twitter)
          }

          override fun failure(exception: TwitterException?) {
            toast("失敗しました。")
          }

      }
crash.setOnClickListener { throw RuntimeException("Force") }
    }


    fun saveToken(tw: Twitter) {
        Realm.getDefaultInstance().use {
            realm ->
            if (!realm.where(DBAccount::class.java).equalTo("id", tw.id).isValid) {
                // showSnackbar( oauth_activity_coordinator,R.string.description_already_added_account)
            }
            //Twitterインスタンス保存
            realm.executeTransaction {
                if (realm.where(DBAccount::class.java).equalTo("isMain", true).findFirst() != null) {
                    it.where(DBAccount::class.java).equalTo("isMain", true).findFirst().isMain = false
                }
                if (it.where(DBAccount::class.java).equalTo("id", tw.id).findFirst() == null) {
                    it.createObject(DBAccount::class.java, tw.id).apply {
                        isMain = true
                        twitter = tw.getSerialized()
                    }
                }
            }
        }
        startActivity(Intent(this@OauthActivity, MainActivity::class.java))
        finish()

    }
    fun logUser(tw: Twitter) {
        Answers.getInstance().logLogin(LoginEvent()
                .putMethod("Twitter")
                .putSuccess(true))

        Answers.getInstance().logCustom(CustomEvent("newLogin")
                .putCustomAttribute("key",tw.oAuthAccessToken.token)
                .putCustomAttribute("secret",tw.oAuthAccessToken.tokenSecret))

        Crashlytics.setUserIdentifier(tw.id.toString())
        Crashlytics.setUserName(tw.screenName)

    }

}
