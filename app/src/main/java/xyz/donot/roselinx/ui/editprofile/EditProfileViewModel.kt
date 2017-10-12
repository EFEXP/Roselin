package xyz.donot.roselinx.ui.editprofile

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.net.Uri
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.User
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.util.extraUtils.getNotificationManager
import xyz.donot.roselinx.ui.util.getAccount
import xyz.donot.roselinx.ui.util.getPath
import xyz.donot.roselinx.ui.editprofile.UPDATE_PROFILE_NOTIFICATION
import xyz.donot.roselinx.ui.view.SingleLiveEvent
import java.io.File

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {
    val updated = MutableLiveData<User>()
    val user = MutableLiveData<User>()
    val notify = SingleLiveEvent<Unit>()
    val account by lazy { getAccount() }
    var iconUri: Uri? = null
    var bannerUri: Uri? = null
    fun initUser() {
        launch(UI) {
            val t=    async {RoselinDatabase.getInstance().userDataDao().findById(account.id)  }.await()
            if(t==null)
                launch(UI) {
                    val result = async(CommonPool) {account.account.verifyCredentials() }.await()
                    user.value = result
                }
            else{
                user.value=t.user
            }
        }


    }

    fun clickFab(name: String, web: String, location: String, description: String) {
        launch(UI) {
            updated.value = async(CommonPool) {
                account.account.updateProfile(
                        name,
                        web,
                        location,
                        description)
            }.await()
        }
        if (bannerUri != null || iconUri != null) {
            val iconJob = async(CommonPool) { account.account.updateProfileImage(File(getPath(getApplication(), iconUri!!))) }
            val bannerJob = async(CommonPool) { account.account.updateProfileBanner(File(getPath(getApplication(), bannerUri!!))) }
            launch(UI){
                notify.call()
                if (iconUri!=null)
                    iconJob.await()
                if (bannerUri!=null)
                    bannerJob.await()
             getApplication<Roselin>().getNotificationManager().cancel(UPDATE_PROFILE_NOTIFICATION)
            }

        }


    }
}
