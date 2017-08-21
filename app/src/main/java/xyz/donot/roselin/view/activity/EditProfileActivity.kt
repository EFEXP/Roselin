package xyz.donot.roselin.view.activity

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.content_edit_profile.*
import twitter4j.Twitter
import twitter4j.User
import xyz.donot.quetzal.util.getPath
import xyz.donot.roselin.R
import xyz.donot.roselin.extend.SafeAsyncTask
import xyz.donot.roselin.util.extraUtils.longToast
import xyz.donot.roselin.util.getSerialized
import xyz.donot.roselin.util.getTwitterInstance
import java.io.File
import java.util.*

class EditProfileActivity : AppCompatActivity() {
  var iconUri: Uri?=null
  var bannerUri: Uri?=null

  override fun onActivityResult(requestCode:Int , resultCode: Int, data: Intent?){
    if (resultCode == RESULT_OK&&data!=null) {
      when(requestCode)
      {
      3->{
        iconUri =UCrop.getOutput(data)
          Picasso.with(this@EditProfileActivity).load(iconUri).into(icon)
      }
        4->{
          bannerUri =UCrop.getOutput(data)
          Picasso.with(this@EditProfileActivity).load(bannerUri).into(profile_banner)
        }

      }
    }
  }

  override fun onBackPressed() {
     AlertDialog.Builder(this@EditProfileActivity)
            .setTitle("戻る")
            .setMessage("編集を削除して戻りますか？")
            .setPositiveButton("はい",  { _,  _->   super.onBackPressed() })
            .setNegativeButton("いいえ",{ dialogInterface, i -> dialogInterface.cancel()})
            .show()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
    val color=ContextCompat.getColor(this@EditProfileActivity,R.color.colorPrimary)
      toolbar.setNavigationOnClickListener { onBackPressed() }
      class userTask:SafeAsyncTask<Twitter,User>(){
          override fun doTask(arg: Twitter): User {
              return  arg.verifyCredentials()
          }

          override fun onSuccess(result: User) {
              Picasso.with(this@EditProfileActivity).load(result.profileBannerIPadRetinaURL).into(profile_banner)
              Picasso.with(this@EditProfileActivity).load(result.originalProfileImageURLHttps).into(icon)
              web.text.insert(0,result.urlEntity.expandedURL)
              user_name.text.insert(0,result.name)
              geo.text.insert(0,result.location)
              description.text.insert(0,result.description)
              profile_banner.setOnClickListener{
                  RxImagePicker.with(this@EditProfileActivity).requestImage(Sources.GALLERY)
                          .subscribe {
                              bannerUri = it
                              UCrop.of(bannerUri!!,Uri.fromFile(File(cacheDir,"${Date().time}.jpg")))
                                      .withOptions( UCrop.Options().apply {
                                          setToolbarColor(color)
                                          setActiveWidgetColor(color)
                                          setStatusBarColor(color)
                                          setAllowedGestures(UCropActivity.SCALE, UCropActivity.SCALE, UCropActivity.SCALE)
                                      })
                                      .withAspectRatio(3F,1F)
                                      .start(this@EditProfileActivity,4)
                          }

              }
              icon.setOnClickListener{
                  RxImagePicker.with(this@EditProfileActivity).requestImage(Sources.GALLERY)
                          .subscribe {
                              iconUri = it
                              UCrop.of(iconUri!!,Uri.fromFile(File(cacheDir,"${Date().time}.jpg")))
                                      .withOptions( UCrop.Options().apply {
                                          setToolbarColor(color)
                                          setActiveWidgetColor(color)
                                          setStatusBarColor(color)
                                          setAllowedGestures(UCropActivity.SCALE, UCropActivity.SCALE, UCropActivity.SCALE)
                                      })
                                      .withAspectRatio(1F,1F)
                                      .start(this@EditProfileActivity,3)
                          }

              }
          }

          override fun onFailure(exception: Exception) {

          }
      }
      userTask().execute(getTwitterInstance())




        fab.setOnClickListener {
    object : SafeAsyncTask<Twitter,User>(){
          override fun doTask(arg: Twitter): User {
                return  arg.updateProfile(
                          user_name.text.toString(),
                          geo.text.toString(),
                          description.text.toString(),
                          web.text.toString())
          }

          override fun onSuccess(result: User) {
            //  longToast("更新しました")
              finish()
              val bundle =  Bundle()
              bundle.putByteArray("user",result.getSerialized())
              setResult(RESULT_OK,Intent().putExtras(bundle))
          }

          override fun onFailure(exception: Exception) {
              longToast("失敗しました")
          }
      }.execute(getTwitterInstance())

            val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (bannerUri != null || iconUri != null) {
                val id = Random().nextInt(100) + 1
                notifiy(id)
                if (iconUri != null) {
                    object : SafeAsyncTask<Twitter,User>(){
                        override fun doTask(arg: Twitter): User {
                            return  arg.updateProfileImage(File(getPath(this@EditProfileActivity, iconUri!!)))
                        }

                        override fun onSuccess(result: User) {
                            longToast("更新しました")
                            mNotificationManager.cancel(id)
                        }

                        override fun onFailure(exception: Exception) {
                            longToast("失敗しました")
                            mNotificationManager.cancel(id)
                        }
                    }.execute(getTwitterInstance())

                } else if (bannerUri != null) {
                    object : SafeAsyncTask<Twitter,Unit>(){
                        override fun doTask(arg: Twitter) {
                            return  arg.updateProfileBanner(File(getPath(this@EditProfileActivity, bannerUri!!)))
                        }

                        override fun onSuccess(result: Unit) {
                            longToast("更新しました")
                            mNotificationManager.cancel(id)
                        }

                        override fun onFailure(exception: Exception) {
                            longToast("失敗しました")
                            mNotificationManager.cancel(id)
                        }
                    }.execute(getTwitterInstance())
                }


          }

        }
    }
    private fun notifiy(int:Int) {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mNotification = NotificationCompat.Builder(this,"Sending")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("更新中")
                .setProgress(100,100,true)
                .setContentText("プロフィールを更新中…")
                .build()
        mNotificationManager.notify(int, mNotification )

    }

}
