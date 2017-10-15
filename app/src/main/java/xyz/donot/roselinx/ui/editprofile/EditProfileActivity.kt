package xyz.donot.roselinx.ui.editprofile

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import xyz.donot.roselinx.R
import xyz.donot.roselinx.ui.util.extraUtils.getNotificationManager
import xyz.donot.roselinx.ui.util.extraUtils.newNotification
import xyz.donot.roselinx.ui.util.getSerialized
import java.io.File
import java.util.*

const val UPDATE_PROFILE_NOTIFICATION=40
class EditProfileActivity : AppCompatActivity() {
    private val viewmodel by lazy { ViewModelProviders.of(this).get(EditProfileViewModel::class.java) }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                3 -> {
                    viewmodel.  iconUri = UCrop.getOutput(data)
                    Picasso.with(this@EditProfileActivity).load(viewmodel.iconUri).into(icon)
                }
                4 -> {
                    viewmodel.  bannerUri = UCrop.getOutput(data)
                    Picasso.with(this@EditProfileActivity).load(viewmodel.bannerUri).into(profile_banner)
                }

            }
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this@EditProfileActivity)
                .setTitle(getString(R.string.dialog_back))
                .setMessage(getString(R.string.dialog_question_delete_back))
                .setPositiveButton(getString(R.string.dialog_OK), { _, _ -> super.onBackPressed() })
                .setNegativeButton(getString(R.string.dialog_cancel), { dialogInterface, _ -> dialogInterface.cancel() })
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
        viewmodel.initUser()
        viewmodel.user.observe(this,android.arch.lifecycle.Observer {
            it?.let {
                result->
                Picasso.with(this@EditProfileActivity).load(result.profileBannerIPadRetinaURL).into(profile_banner)
                Picasso.with(this@EditProfileActivity).load(result.originalProfileImageURLHttps).into(icon)
                web.text.insert(0, result.urlEntity.expandedURL)
                user_name.text.insert(0, result.name)
                geo.text.insert(0, result.location)
                description.text.insert(0, result.description)
            }
        })
        profile_banner.setOnClickListener {
            RxImagePicker.with(this@EditProfileActivity).requestImage(Sources.GALLERY)
                    .subscribe {
                     viewmodel.bannerUri = it
                        UCrop.of(viewmodel.bannerUri!!, Uri.fromFile(File(cacheDir, "${Date().time}.jpg")))
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
                        viewmodel.iconUri = it
                        UCrop.of(viewmodel.iconUri!!, Uri.fromFile(File(cacheDir, "${Date().time}.jpg")))
                                .withOptions(UCrop.Options().apply {
                                    setToolbarColor(color)
                                    setActiveWidgetColor(color)
                                    setStatusBarColor(color)
                                    setAllowedGestures(UCropActivity.SCALE, UCropActivity.SCALE, UCropActivity.SCALE)
                                })
                                .withAspectRatio(1F, 1F)
                                .start(this@EditProfileActivity, 3)
                    } }


        fab.setOnClickListener {
            viewmodel.clickFab(
                    user_name.text.toString(),
                    web.text.toString(),
                    geo.text.toString(),
                    description.text.toString())

        }
        viewmodel.updated.observe(this,android.arch.lifecycle.Observer {
            it?.let {
                finish()
                val bundle = Bundle()
                bundle.putByteArray("user",it.getSerialized())
                setResult(RESULT_OK, Intent().putExtras(bundle))
            }
        })
        viewmodel.notify.observe(this,android.arch.lifecycle.Observer{
           getNotificationManager().notify(UPDATE_PROFILE_NOTIFICATION,
                   newNotification({
                       setSmallIcon(R.drawable.ic_launcher)
                       setContentTitle(getString(R.string.notf_title_updating))
                       setProgress(100, 100, true)
                       setContentText(getString(R.string.notf_updating_profile))
                   },"sending"))
        })

    }


}
