package xyz.donot.roselin.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.crashlytics.android.answers.LoginEvent
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_oauth.*
import kotlinx.android.synthetic.main.content_oauth.*
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import xyz.donot.roselin.R
import xyz.donot.roselin.model.realm.DBAccount
import xyz.donot.roselin.model.realm.DBMute
import xyz.donot.roselin.util.extraUtils.async
import xyz.donot.roselin.util.extraUtils.mainThread
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
        setSupportActionBar(toolbar)

      login_button.callback= object : Callback<TwitterSession>() {
          override fun success(result: Result<TwitterSession>) {
              val builder= ConfigurationBuilder()
              builder.setOAuthConsumerKey(
                      //  getString(R.string.twitter_official_consumer_key)
                          getString(R.string.twitter_consumer_key)
              )
              builder.setOAuthConsumerSecret(
                    // getString(R.string.twitter_official_consumer_secret)
                          getString(R.string.twitter_consumer_secret)
              )
              builder.setTweetModeExtended(true)
              builder.setOAuthAccessToken(result.data.authToken.token)
              builder.setOAuthAccessTokenSecret(result.data.authToken.secret)

              val twitter=TwitterFactory(builder.build()).instance
              logUser(twitter)
              saveToken(twitter)
              saveMute(twitter)
          }

          override fun failure(exception: TwitterException?) = toast("失敗しました。")

      }

    }


    fun saveToken(tw: Twitter) {
        async {
            try {
                val result=tw.verifyCredentials()
            mainThread {
                if(result!=null){
                    Realm.getDefaultInstance().executeTransaction {
                        realm ->
                        val realmAccounts=realm.where(DBAccount::class.java).equalTo("isMain", true)
                        //Twitterインスタンス保存
                        if (realmAccounts.findFirst() != null) {
                            realmAccounts.findFirst().isMain = false
                        }
                        if (realm.where(DBAccount::class.java).equalTo("id",result.id).findFirst() == null) {
                            realm.createObject(DBAccount::class.java,result.id).apply {
                                isMain = true
                                twitter = tw.getSerialized()
                                user=result.getSerialized()
                            }
                        }
                    }
                    finish()
                    startActivity(Intent(this@OauthActivity, MainActivity::class.java))
                }}


            }
            catch (e:Exception){
                toast(e.localizedMessage)
            }
        }
    }
    fun  saveMute(tw: Twitter){
        var cursor: Long = -1L
        val arrayMute=ArrayList<Long>()
            async {
               val result = tw.getMutesIDs(cursor)
                if (result!=null) {
                    mainThread { arrayMute.addAll(result.iDs.toList()) }
                    Realm.getDefaultInstance().executeTransaction {
                        realm ->
                        arrayMute.forEach { ids->
                            realm.createObject(DBMute::class.java).apply {
                                id=ids
                            }
                        }

                    }
                }
            }


    }
    fun logUser(tw: Twitter) {
        async {
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

}
