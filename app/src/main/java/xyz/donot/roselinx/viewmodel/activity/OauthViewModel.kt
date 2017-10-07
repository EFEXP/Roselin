package xyz.donot.roselinx.viewmodel.activity

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
import xyz.donot.roselinx.model.realm.AccountObject
import xyz.donot.roselinx.model.realm.MuteObject
import xyz.donot.roselinx.model.room.UserData
import xyz.donot.roselinx.util.extraUtils.Bundle
import xyz.donot.roselinx.util.getSerialized
import xyz.donot.roselinx.view.custom.SingleLiveEvent

class OauthViewModel(app: Application) : AndroidViewModel(app) {
    private val realm by lazy { Realm.getDefaultInstance() }
    val information = MutableLiveData<String>()
    val isFinished = SingleLiveEvent<Unit>()
    private fun saveToken(tw: Twitter, user: User) {
            val realmAccounts = realm.where(AccountObject::class.java).equalTo("isMain", true)
            realm.executeTransaction {
                if (realmAccounts.findFirst() != null) {
                    realmAccounts.findFirst()?.isMain = false
                }
               realm.insertOrUpdate(
                       AccountObject().also {
                           it.twitter = tw.getSerialized()
                           it.isMain = true
                           it.user = user.getSerialized()
                           it.id = user.id
                       }
               )
            }
        UserData.save(getApplication(),user)

    }

    private var hasNext = false
    private var cursor: Long = -1L
    private fun saveMute(tw: Twitter) {
        launch(UI) {
            try {
                val result = async(CommonPool) { tw.getMutesList(cursor) }.await()
                hasNext = result.hasNext()
                if (result.hasNext()) cursor = result.nextCursor
                realm.executeTransaction {
                    result.forEach { muser ->
                        realm.insertOrUpdate(
                                MuteObject().apply {
                                    user = muser.getSerialized()
                                    id = muser.id
                                }
                        )
                    }
                }
                if (hasNext) saveMute(tw)
                else isFinished.call()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveFollower(tw: Twitter, u: User) {
        launch(UI) {
            val result = async(CommonPool) { tw.getFriendsList(u.id, -1, 50) }.await()
            realm.executeTransaction {
                result.forEach { user_ ->
                    UserData.save(getApplication(),user_)
                }
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
        information.value = "ユーザ情報の取得中"
        launch(UI) {
            try {
                val user = async(CommonPool) { twitter.verifyCredentials() }.await()
                logUser(user)
                information.value = "ユーザ情報保存中"
                saveToken(twitter, user)
                information.value = "フォロワー情報取得中"
                saveFollower(twitter, user)
                information.value = "ミュートユーザ取得中"
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