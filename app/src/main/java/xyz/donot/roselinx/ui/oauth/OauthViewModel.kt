﻿package xyz.donot.roselinx.ui.oauth

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.analytics.FirebaseAnalytics
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.User
import twitter4j.conf.ConfigurationBuilder
import xyz.donot.roselinx.model.entity.MuteFilter
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.model.entity.TwitterAccount
import xyz.donot.roselinx.model.entity.UserData
import xyz.donot.roselinx.ui.util.extraUtils.bundle
import xyz.donot.roselinx.ui.view.SingleLiveEvent

class OauthViewModel(app: Application) : AndroidViewModel(app) {

    val information = MutableLiveData<String>()
    val isFinished = SingleLiveEvent<Unit>()
    private fun saveToken(tw: Twitter, user: User) {
        launch {
            val dao = RoselinDatabase.getInstance().twitterAccountDao()
            val count = async { dao.count() }.await()
            if (count > 0) {
                val account = async { dao.getMainAccount(true).copy(isMain = false) }.await()
                dao.update(account)
            }
            dao.insertUser(TwitterAccount(isMain = true, user = user, id = user.id, account = tw))
        }
        UserData.save(UserData(user = user,id = user.id,screenname = user.screenName))

    }

    private var hasNext = false
    private var cursor: Long = -1L
    private fun saveMute(tw: Twitter) {
        launch(UI) {
            try {
                val result = async(CommonPool) { tw.getMutesList(cursor) }.await()
                hasNext = result.hasNext()
                if (result.hasNext()) cursor = result.nextCursor
                result.forEach { muser ->
                    MuteFilter.save(MuteFilter(user = muser, accountId = muser.id))
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
            UserData.saveAll(result.map { UserData(it,it.screenName,it.id) }.toTypedArray())
        }
    }

    private fun logUser(user: User) {
        FirebaseAnalytics.getInstance(getApplication()).apply {
            setUserProperty("screenname", user.screenName)
            setUserId(user.id.toString())
        }.logEvent(FirebaseAnalytics.Event.LOGIN, bundle {
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


}