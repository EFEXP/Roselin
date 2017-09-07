package xyz.donot.roselinx.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterSession
import io.realm.Realm
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import xyz.donot.roselinx.model.realm.DBAccount
import xyz.donot.roselinx.model.realm.DBMute
import xyz.donot.roselinx.util.getSerialized

class OauthViewModel : ViewModel() {
	private val realm by lazy { Realm.getDefaultInstance()  }
	val isFinished: MutableLiveData<Unit> = MutableLiveData()


	private fun saveToken(tw: Twitter) {
		launch(UI) {
			try {
				val result = async(CommonPool) { tw.verifyCredentials() }.await()
				val realmAccounts = realm.where(DBAccount::class.java).equalTo("isMain", true)
				//Twitterインスタンス保存
				if (realmAccounts.findFirst() != null) {
					realmAccounts.findFirst()?.isMain = false
				}
				if (realm.where(DBAccount::class.java).equalTo("id", result.id).findAll().count() == 0) {
					realm.executeTransaction {
						val account = realm.createObject(DBAccount::class.java, result.id)
						account.isMain = true
						account.twitter = tw.getSerialized()
						account.user = result.getSerialized()
					}
				}
			} catch (e: Exception) {
				e.printStackTrace()
			}

		}

	}

	private fun saveMute(tw: Twitter) {
		var cursor: Long = -1L
		launch(UI) {
			try {
				val result = async(CommonPool) { tw.getMutesList(cursor) }.await()
				realm.executeTransaction { result.forEach {
					muser ->
						realm.createObject(DBMute::class.java).apply {
							user = muser.getSerialized()
							id = muser.id
						}
					}

				}
				isFinished.value=Unit
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

	fun onSuccess(key: String, secret: String, result: Result<TwitterSession>) {
		val builder = ConfigurationBuilder()
		builder.setOAuthConsumerKey(key)
		builder.setOAuthConsumerSecret(secret)
		builder.setTweetModeExtended(true)
		builder.setOAuthAccessToken(result.data.authToken.token)
		builder.setOAuthAccessTokenSecret(result.data.authToken.secret)
		val twitter = TwitterFactory(builder.build()).instance
		//logUser(twitter)
		saveToken(twitter)
		saveMute(twitter)
	}

	override fun onCleared() {
		super.onCleared()
		realm.close()
	}

}