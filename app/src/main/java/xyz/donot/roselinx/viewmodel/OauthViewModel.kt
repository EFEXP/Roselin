package xyz.donot.roselinx.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.analytics.FirebaseAnalytics
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterSession
import io.realm.Realm
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.User
import twitter4j.conf.ConfigurationBuilder
import xyz.donot.roselinx.model.realm.DBAccount
import xyz.donot.roselinx.model.realm.DBMute
import xyz.donot.roselinx.util.extraUtils.Bundle
import xyz.donot.roselinx.util.getSerialized

class OauthViewModel(app: Application) : AndroidViewModel(app) {
    private val realm by lazy { Realm.getDefaultInstance() }
    val isFinished: MutableLiveData<Unit> by lazy { MutableLiveData<Unit>() }


    private fun saveToken(tw: Twitter, user: User) {
        //val result = async(CommonPool) { tw.verifyCredentials() }.await()
        if (realm.where(DBAccount::class.java).equalTo("id", user.id).findAll().count() == 0) {

            val realmAccounts = realm.where(DBAccount::class.java).equalTo("isMain", true)
            realm.executeTransaction {
                if (realmAccounts.findFirst() != null) {
                    realmAccounts.findFirst()?.isMain = false
                }
                val account = realm.createObject(DBAccount::class.java, user.id)
                account.isMain = true
                account.twitter = tw.getSerialized()
                account.user = user.getSerialized()
            }
        }
    }

    private fun saveMute(tw: Twitter) {
        var cursor: Long = -1L
        launch(UI) {
            try {
                val result = async(CommonPool) { tw.getMutesList(cursor) }.await()
                realm.executeTransaction {
                    result.forEach { muser ->
                        realm.createObject(DBMute::class.java).apply {
                            user = muser.getSerialized()
                            id = muser.id
                        }
                    }

                }
                isFinished.value = Unit
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun logUser(user: User) {
        FirebaseAnalytics.getInstance(getApplication()).apply {
            setUserProperty("screenname", user.screenName)
            setUserId(user.id.toString())
        }.logEvent(FirebaseAnalytics.Event.LOGIN, Bundle {
            putString(FirebaseAnalytics.Param.CONTENT, user.screenName)
            putString("UserName", user.name)
        })


    }

    fun onSuccess(key: String, secret: String, result: Result<TwitterSession>) {
        val builder = ConfigurationBuilder()
                .apply {
                    setJSONStoreEnabled(true)
                    setOAuthConsumerKey(key)
                    setOAuthConsumerSecret(secret)
                    setTweetModeExtended(true)
                    setOAuthAccessToken(result.data.authToken.token)
                    setOAuthAccessTokenSecret(result.data.authToken.secret)
                }.build()
        val twitter = TwitterFactory(builder).instance
        launch(UI) {
            try {
                val user = async(CommonPool) { twitter.verifyCredentials() }.await()
                logUser(user)
                saveToken(twitter, user)
                saveMute(twitter)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

}