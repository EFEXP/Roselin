package xyz.donot.roselinx.viewmodel.activity

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
import xyz.donot.roselinx.model.room.RoselinDatabase
import xyz.donot.roselinx.util.extraUtils.getNotificationManager
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.util.getPath
import xyz.donot.roselinx.util.getTwitterInstance
import xyz.donot.roselinx.view.activity.UPDATE_PROFILE_NOTIFICATION
import xyz.donot.roselinx.view.custom.SingleLiveEvent
import java.io.File

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {
    val updated = MutableLiveData<User>()
    val user = MutableLiveData<User>()
    val notify = SingleLiveEvent<Unit>()
    var iconUri: Uri? = null
    var bannerUri: Uri? = null
    fun initUser() {
        launch(UI) {
            val t=    async {RoselinDatabase.getInstance(getApplication()).userDataDao().findById(getMyId())  }.await()
            if(t==null)
                launch(UI) {
                    val result = async(CommonPool) { getTwitterInstance().verifyCredentials() }.await()
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
                getTwitterInstance().updateProfile(
                        name,
                        web,
                        location,
                        description)
            }.await()
        }
        if (bannerUri != null || iconUri != null) {
            val iconJob = async(CommonPool) { getTwitterInstance().updateProfileImage(File(getPath(getApplication(), iconUri!!))) }
            val bannerJob = async(CommonPool) { getTwitterInstance().updateProfileBanner(File(getPath(getApplication(), bannerUri!!))) }
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
