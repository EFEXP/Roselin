package xyz.donot.roselinx.viewmodel.activity

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
import xyz.donot.roselinx.model.realm.CustomProfileObject
import xyz.donot.roselinx.model.realm.MuteObject
import xyz.donot.roselinx.model.room.RoselinDatabase
import xyz.donot.roselinx.model.room.UserData
import xyz.donot.roselinx.util.extraUtils.toast
import xyz.donot.roselinx.util.extraUtils.twitterExceptionMessage
import xyz.donot.roselinx.util.getSerialized
import xyz.donot.roselinx.util.getTwitterInstance


class UserViewModel(app: Application) : AndroidViewModel(app) {
    var mUser: MutableLiveData<User> = MutableLiveData()
    private val realm by lazy { Realm.getDefaultInstance() }
    fun initUser(screenName: String) {
        if (mUser.value == null) {
       // val user=    realm.where(UserObject::class.java).equalTo("screenname",screenName).findFirst()
          //  user?.let {
          //      mUser.value=user.user.getDeserialized()
         //   }?:
            launch(UI) {
                try {
                    val result= async(CommonPool) { getTwitterInstance().showUser(screenName) }.await()
                    mUser.value =result
                    UserData.save(getApplication(),result)
                } catch (e: TwitterException) {
                    launch(UI) {
                      val user=  async {
                        RoselinDatabase.getInstance(getApplication()).userDataDao()
                                .findByScreenName(screenName)
                                .user}.await()
                        mUser.value=user
                        getApplication<Roselin>().toast(twitterExceptionMessage(e))
                    }
                }
            }
        }
    }
    fun initUser(id:Long) {
        if (mUser.value == null) {
          //  val user=    realm.where(UserObject::class.java).equalTo("id",id).findFirst()
        //    user?.let {
         //       mUser.value=user.user.getDeserialized()
         //   }?:
            launch(UI) {
                try {
                    val result= async(CommonPool) { getTwitterInstance().showUser(id) }.await()
                    mUser.value =result
                    UserData.save(getApplication(),result)
                } catch (e: TwitterException) {
                    val user=  async(CommonPool) { RoselinDatabase.getInstance(getApplication()).userDataDao().findById(id)}.await()
                    mUser.value =user.user
                    getApplication<Roselin>().toast(twitterExceptionMessage(e))
                }
            }
        }
    }

    fun muteUser() {
        realm.executeTransaction {
            it.createObject(MuteObject::class.java)
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
                    CustomProfileObject().apply {
                        id = mUser.value!!.id
                        customname = string
                    }
            )
        }
        getApplication<Roselin>().toast("変更しました")
    }

    fun revertName(){
        realm.executeTransaction {
            it.where(CustomProfileObject::class.java).equalTo("id",mUser.value!!.id).findAll().forEach {
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