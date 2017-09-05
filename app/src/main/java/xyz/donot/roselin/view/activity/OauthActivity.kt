package xyz.donot.roselin.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_oauth.*
import kotlinx.android.synthetic.main.content_oauth.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import xyz.donot.roselin.R
import xyz.donot.roselin.model.realm.DBAccount
import xyz.donot.roselin.model.realm.DBMute
import xyz.donot.roselin.util.extraUtils.logw
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.util.getSerialized
import kotlin.properties.Delegates


class OauthActivity : AppCompatActivity() {

	private var realm: Realm by Delegates.notNull()
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		login_button.onActivityResult(requestCode, resultCode, data)
	}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_oauth)
		setSupportActionBar(toolbar)
		realm = Realm.getDefaultInstance()
		login_button.callback = object : Callback<TwitterSession>() {
			override fun success(result: Result<TwitterSession>) {
				val builder = ConfigurationBuilder()
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
				val twitter = TwitterFactory(builder.build()).instance
				//logUser(twitter)
				saveToken(twitter)
				saveMute(twitter)
			}

			override fun failure(exception: TwitterException?) = toast("失敗しました。")

		}

	}


	fun saveToken(tw: Twitter) {
		launch(UI) {
			try {
				val result = async(CommonPool) { tw.verifyCredentials() }.await()
					val realmAccounts = realm.where(DBAccount::class.java).equalTo("isMain", true)
					//Twitterインスタンス保存
					if (realmAccounts.findFirst() != null) {
						realmAccounts.findFirst()?.isMain = false
					}
					if (realm.where(DBAccount::class.java).equalTo("id", result.id).findAll().count()==0) {
						realm.executeTransaction {
							val account=realm.createObject(DBAccount::class.java,result.id)
							account.isMain = true
							account.twitter = tw.getSerialized()
							account.user = result.getSerialized()
						}
					}
			} catch (e: Exception) {
				logw(e.message+"")
				toast(e.localizedMessage)
			}

		}

	}

	fun saveMute(tw: Twitter) {
		var cursor: Long = -1L
		launch(UI) {
			try {
				val result = async(CommonPool) {
					tw.getMutesList(cursor)
				}.await()
				realm.executeTransaction {
					result.forEach { muser ->
						realm.createObject(DBMute::class.java).apply {
							user = muser.getSerialized()
							id = muser.id
						}
					}

				}
				finish()
				startActivity(Intent(this@OauthActivity, MainActivity::class.java))
			}
			catch (e:Exception){
				logw(e.message+"")
			}
		}


	}

	fun logUser(tw: Twitter) {
		Thread{ Runnable {
		//	Answers.getInstance().logLogin(("Twitter")
				//	.putSuccess(true))

			//Answers.getInstance().logCustom(CustomEvent("newLogin")
			//		.putCustomAttribute("key", tw.oAuthAccessToken.token)
			//		.putCustomAttribute("secret", tw.oAuthAccessToken.tokenSecret))

		//	Crashlytics.setUserIdentifier(tw.id.toString())
			//Crashlytics.setUserName(tw.screenName)

		}}.start()
	}

}
