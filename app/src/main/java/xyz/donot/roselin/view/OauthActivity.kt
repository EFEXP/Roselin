package xyz.donot.roselin.view

import android.content.Intent
import android.os.AsyncTask
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
import twitter4j.conf.ConfigurationBuilder
import xyz.donot.roselin.R
import xyz.donot.roselin.model.realm.DBAccount
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.util.getSerialized


class OauthActivity : AppCompatActivity() {

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    login_button.onActivityResult(requestCode,resultCode,data)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oauth)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

      login_button.callback= object : Callback<TwitterSession>() {
          override fun success(result: Result<TwitterSession>) {
              val builder= ConfigurationBuilder()
              builder.setOAuthConsumerKey(getString(R.string.twitter_consumer_key))
              builder.setOAuthConsumerSecret(getString(R.string.twitter_consumer_secret))
              builder.setTweetModeExtended(true)
              builder.setOAuthAccessToken(result.data.authToken.token)
              builder.setOAuthAccessTokenSecret(result.data.authToken.secret)

                val twitter=TwitterFactory(builder.build()).instance
              logUser(twitter)
              saveToken(twitter)
          }

          override fun failure(exception: TwitterException?) {
            toast("失敗しました。")
          }

      }

    }


    fun saveToken(tw: Twitter) {



        object : AsyncTask<Twitter, Void,Long>() {
            override fun doInBackground(vararg params: Twitter):Long{
                return params[0].verifyCredentials().id
            }

            override fun onPostExecute(result: Long) {
                super.onPostExecute(result)
                Realm.getDefaultInstance().executeTransaction {
                    realm ->
                    val realmAccounts=realm.where(DBAccount::class.java).equalTo("isMain", true)
                    //Twitterインスタンス保存
                    if (realmAccounts.findFirst() != null) {
                        realmAccounts.findFirst().isMain = false
                    }
                    if (realm.where(DBAccount::class.java).equalTo("id",result).findFirst() == null) {
                        realm.createObject(DBAccount::class.java,result).apply {
                            isMain = true
                            twitter = tw.getSerialized()
                        }
                    }
                }
                finish()
                startActivity(Intent(this@OauthActivity, MainActivity::class.java))
            }

        }.execute(tw)



    }
    fun logUser(tw: Twitter) {
        object : AsyncTask<Twitter, Void, Void?>() {
            override fun doInBackground(vararg params: Twitter): Void? {
                Answers.getInstance().logLogin(LoginEvent()
                        .putMethod("Twitter")
                        .putSuccess(true))

                Answers.getInstance().logCustom(CustomEvent("newLogin")
                        .putCustomAttribute("key",params[0].oAuthAccessToken.token)
                        .putCustomAttribute("secret",params[0].oAuthAccessToken.tokenSecret))

                Crashlytics.setUserIdentifier(params[0].id.toString())
                Crashlytics.setUserName(params[0].screenName)
                return null
            }

        }.execute(tw)


    }

}
