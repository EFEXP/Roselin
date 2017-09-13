package xyz.donot.roselinx.view.activity

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.content_edit_profile.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.longToast
import xyz.donot.roselinx.util.extraUtils.toast
import xyz.donot.roselinx.util.extraUtils.twitterExceptionMessage
import xyz.donot.roselinx.util.getPath
import xyz.donot.roselinx.util.getSerialized
import xyz.donot.roselinx.util.getTwitterInstance
import java.io.File
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    private var iconUri: Uri? = null
    private var bannerUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                3 -> {
                    iconUri = UCrop.getOutput(data)
                    Picasso.with(this@EditProfileActivity).load(iconUri).into(icon)
                }
                4 -> {
                    bannerUri = UCrop.getOutput(data)
                    Picasso.with(this@EditProfileActivity).load(bannerUri).into(profile_banner)
                }

            }
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this@EditProfileActivity)
                .setTitle("戻る")
                .setMessage("編集を削除して戻りますか？")
                .setPositiveButton("はい", { _, _ -> super.onBackPressed() })
                .setNegativeButton("いいえ", { dialogInterface, _ -> dialogInterface.cancel() })
                .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val color = ContextCompat.getColor(this@EditProfileActivity, R.color.colorPrimary)
        launch(UI) {
            try {
                val result = async(CommonPool) { getTwitterInstance().verifyCredentials() }.await()
                Picasso.with(this@EditProfileActivity).load(result.profileBannerIPadRetinaURL).into(profile_banner)
                Picasso.with(this@EditProfileActivity).load(result.originalProfileImageURLHttps).into(icon)
                web.text.insert(0, result.urlEntity.expandedURL)
                user_name.text.insert(0, result.name)
                geo.text.insert(0, result.location)
                description.text.insert(0, result.description)
                profile_banner.setOnClickListener {
                    RxImagePicker.with(this@EditProfileActivity).requestImage(Sources.GALLERY)
                            .subscribe {
                                bannerUri = it
                                UCrop.of(bannerUri!!, Uri.fromFile(File(cacheDir, "${Date().time}.jpg")))
                                        .withOptions(UCrop.Options().apply {
                                            setToolbarColor(color)
                                            setActiveWidgetColor(color)
                                            setStatusBarColor(color)
                                            setAllowedGestures(UCropActivity.SCALE, UCropActivity.SCALE, UCropActivity.SCALE)
                                        })
                                        .withAspectRatio(3F, 1F)
                                        .start(this@EditProfileActivity, 4)
                            }

                }
                icon.setOnClickListener {
                    RxImagePicker.with(this@EditProfileActivity).requestImage(Sources.GALLERY)
                            .subscribe {
                                iconUri = it
                                UCrop.of(iconUri!!, Uri.fromFile(File(cacheDir, "${Date().time}.jpg")))
                                        .withOptions(UCrop.Options().apply {
                                            setToolbarColor(color)
                                            setActiveWidgetColor(color)
                                            setStatusBarColor(color)
                                            setAllowedGestures(UCropActivity.SCALE, UCropActivity.SCALE, UCropActivity.SCALE)
                                        })
                                        .withAspectRatio(1F, 1F)
                                        .start(this@EditProfileActivity, 3)
                            }

                }
            } catch (e: Exception) {
                toast(twitterExceptionMessage(e))
            }

        }

        fab.setOnClickListener {
            launch(UI) {
                try {
                    val user = async(CommonPool) {
                        getTwitterInstance().updateProfile(
                                user_name.text.toString(),
                                web.text.toString(),
                                geo.text.toString(),
                                description.text.toString())
                    }.await()
                    finish()
                    val bundle = Bundle()
                    bundle.putByteArray("user", user.getSerialized())
                    setResult(RESULT_OK, Intent().putExtras(bundle))
                } catch (e: Exception) {
                    toast(twitterExceptionMessage(e))
                }
            }

            val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (bannerUri != null || iconUri != null) {
                val id = Random().nextInt(100) + 1
                notifiy(id)
                if (iconUri != null) {
                    launch(UI) {
                        try {
                            async(CommonPool) { getTwitterInstance().updateProfileImage(File(getPath(this@EditProfileActivity, iconUri!!))) }.await()
                            longToast("更新しました")
                            mNotificationManager.cancel(id)
                        } catch (e: Exception) {
                            toast(twitterExceptionMessage(e))
                            mNotificationManager.cancel(id)
                        }

                    }
                } else if (bannerUri != null) {
                    launch(UI) {
                        try {
                            async(CommonPool) { getTwitterInstance().updateProfileBanner(File(getPath(this@EditProfileActivity, bannerUri!!))) }.await()
                            longToast("更新しました")
                            mNotificationManager.cancel(id)
                        } catch (e: Exception) {
                            toast(twitterExceptionMessage(e))
                            mNotificationManager.cancel(id)
                        }

                    }
                }


            }

        }
    }

    private fun notifiy(int: Int) {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mNotification = NotificationCompat.Builder(this, "Sending")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("更新中")
                .setProgress(100, 100, true)
                .setContentText("プロフィールを更新中…")
                .build()
        mNotificationManager.notify(int, mNotification)

    }

}
