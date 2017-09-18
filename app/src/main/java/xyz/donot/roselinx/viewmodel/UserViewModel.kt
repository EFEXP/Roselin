package xyz.donot.roselinx.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import io.realm.Realm
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.TwitterException
import twitter4j.User
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.model.realm.DBCustomProfile
import xyz.donot.roselinx.model.realm.DBMute
import xyz.donot.roselinx.model.realm.DBUser
import xyz.donot.roselinx.model.realm.saveUser
import xyz.donot.roselinx.util.extraUtils.toast
import xyz.donot.roselinx.util.extraUtils.twitterExceptionMessage
import xyz.donot.roselinx.util.getDeserialized
import xyz.donot.roselinx.util.getSerialized
import xyz.donot.roselinx.util.getTwitterInstance


class UserViewModel(app: Application) : AndroidViewModel(app) {
    var mUser: MutableLiveData<User> = MutableLiveData()
    private val realm by lazy { Realm.getDefaultInstance() }
    fun initUser(screenName: String) {
        if (mUser.value == null) {
            launch(UI) {
                try {
                    val result= async(CommonPool) { getTwitterInstance().showUser(screenName) }.await()
                    mUser.value =result
                    saveUser(result)
                } catch (e: TwitterException) {
                    val user=  realm.where(DBUser::class.java).equalTo("screenname",screenName).findFirst()
                   mUser.value =user?.user?.getDeserialized<User>()
                   getApplication<Roselin>().toast(twitterExceptionMessage(e))
                }
            }
        }
    }
    fun initUser(id:Long) {
        if (mUser.value == null) {
            launch(UI) {
                try {
                    val result= async(CommonPool) { getTwitterInstance().showUser(id) }.await()
                    mUser.value =result
                    saveUser(result)
                } catch (e: TwitterException) {
                    val user=  realm.where(DBUser::class.java).equalTo("id",id).findFirst()
                    mUser.value =user?.user?.getDeserialized<User>()
                    getApplication<Roselin>().toast(twitterExceptionMessage(e))
                }
            }
        }
    }

    fun muteUser() {
        realm.executeTransaction {
            it.createObject(DBMute::class.java)
                    .apply {
                        id = mUser.value!!.id
                        user = mUser.value?.getSerialized()
                    }
        }
        getApplication<Roselin>().toast("ミュートしました")
    }

    fun changeName(string: String) {
        realm.executeTransaction {
            it.copyToRealmOrUpdate(
                    DBCustomProfile().apply {
                        id = mUser.value!!.id
                        customname = string
                    }
            )
        }
        getApplication<Roselin>().toast("変更しました")
    }

    fun revertName(){
        realm.executeTransaction {
            it.where(DBCustomProfile::class.java).equalTo("id",mUser.value!!.id).findAll().forEach {
                it.customname=null
            }
        }
        getApplication<Roselin>().toast("戻しました")
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}