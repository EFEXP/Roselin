package xyz.donot.roselinx.ui.detailuser

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Paging
import twitter4j.TwitterException
import twitter4j.User
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.model.entity.*
import xyz.donot.roselinx.ui.util.extraUtils.toast
import xyz.donot.roselinx.ui.util.extraUtils.twitterExceptionMessage
import xyz.donot.roselinx.ui.util.getAccount
import xyz.donot.roselinx.ui.view.SingleLiveEvent
import kotlin.properties.Delegates


class UserTimeLineViewModel(app: Application) : AndroidViewModel(app) {
    var twitter by Delegates.notNull<TwitterAccount>()
    val dataRefreshed = SingleLiveEvent<Unit>()
    val mainTwitter by lazy { getAccount() }
    var page: Int = 0
        get() {
            field++
            return field
        }
    var mUser: MutableLiveData<User> = MutableLiveData()
    val mUserID =MutableLiveData<Long>()

    fun loadMoreData(hasData:Boolean,userID:Long) {
        launch(UI) {
            try {
                val paging = Paging(page)
                if (hasData) {
                    val oldestId = async { RoselinDatabase.getInstance().tweetDao().getUserOldestTweet(USER_TIMELINE,twitter.id,userID).tweetId}.await()
                    paging.maxId = oldestId
                }
                val result = async { twitter.account.getUserTimeline(userID, paging) }.await()
               Tweet.save(result,USER_TIMELINE,twitter.id)
            } catch (e: TwitterException) {
                //  adapter.loadMoreFail()
                getApplication<Roselin>().toast(twitterExceptionMessage(e))
            }
        }
    }
    fun pullDown(userID:Long) {
        launch(UI) {
            try {
                val newestId = async { RoselinDatabase.getInstance().tweetDao().getUserNewestTweet(USER_TIMELINE,twitter.id,userID).tweetId }.await()
                async {  twitter.account.getHomeTimeline(Paging(newestId)) }.await()?.let {
                    Tweet.save(it,USER_TIMELINE,twitter.id)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                dataRefreshed.call()
            }
        }
    }
    fun initUser(screenName: String) {
        if (mUser.value == null) {
            // val user=    realm.where(UserObject::class.java).equalTo("screenname",screenName).findFirst()
            //  user?.let {
            //      mUser.value=user.user.getDeserialized()
            //   }?:
            launch(UI) {
                try {
                    val result= async(CommonPool) { getAccount().account.showUser(screenName) }.await()
                    mUser.value =result
                    UserData.save(UserData(user = result,id = result.id,screenname = result.screenName))
                } catch (e: TwitterException) {
                    val user=  RoselinDatabase.getInstance().userDataDao().findByScreenName(screenName)
                    mUser.value =user.user
                    getApplication<Roselin>().toast(twitterExceptionMessage(e))
                }
            }
        }
    }
    fun initUser(id:Long) {
        if (mUser.value == null) {
            //  val user=    realm.where(UserObject::class.java).equalTo("tweetId",tweetId).findFirst()
            //    user?.let {
            //       mUser.value=user.user.getDeserialized()
            //   }?:
            launch(UI) {
                try {
                    val result= async(CommonPool) { getAccount().account.showUser(id) }.await()
                    mUser.value =result
                    UserData.save(UserData(user = result,id = result.id,screenname = result.screenName))
                } catch (e: TwitterException) {
                    val user=    RoselinDatabase.getInstance().userDataDao().findById(id)
                    mUser.value =user.user
                    getApplication<Roselin>().toast(twitterExceptionMessage(e))
                }
            }
        }
    }

    fun muteUser() {
        launch(UI) {
            async {
                MuteFilter.save(MuteFilter(accountId =mUser.value!!.id,user = mUser.value))
            }.await()
            getApplication<Roselin>().toast("ミュートしました")
        }
    }



    fun changeName(string: String) {
        CustomProfile.save(CustomProfile(string,mUser.value!!.id))
        getApplication<Roselin>().toast("変更しました")
    }

    fun revertName(){
        CustomProfile.save(CustomProfile(null,mUser.value!!.id))
        getApplication<Roselin>().toast("戻しました")
    }


}