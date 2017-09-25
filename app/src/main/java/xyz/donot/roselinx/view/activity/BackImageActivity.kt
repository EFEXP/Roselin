package xyz.donot.roselinx.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import kotlinx.android.synthetic.main.activity_back_image.*
import kotlinx.android.synthetic.main.content_back_image.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.defaultSharedPreferences
import java.io.File
import java.util.*

class BackImageActivity : AppCompatActivity() {

  private  var bannerUri: Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back_image)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val uriString=defaultSharedPreferences.getString("BackGroundUri","")
        if (!uriString.isNullOrBlank()){
            Picasso.with(this@BackImageActivity).load(uriString).into(iv_background)
        }

       iv_background.setOnClickListener{
           AlertDialog.Builder(this@BackImageActivity)
                   .apply {
                       setTitle("確認")
                       setMessage("削除してもよろしいですか？")
                       setPositiveButton("はい", { _, _ ->
                           iv_background.setImageBitmap(null)
                           defaultSharedPreferences.edit().putString("BackGroundUri","").apply() })
                       setNegativeButton("いいえ", null) }.create().show()

       }




        fab.setOnClickListener { _ ->
            RxImagePicker.with(this@BackImageActivity).requestImage(Sources.GALLERY)
                    .subscribe {
                        bannerUri = it
                        UCrop.of(bannerUri!!, Uri.fromFile(File(filesDir,"${Date().time}.jpg")))
                                .withOptions( UCrop.Options().apply {
                                    setAllowedGestures(UCropActivity.SCALE, UCropActivity.SCALE, UCropActivity.SCALE)
                                })
                                .withAspectRatio(9F,16F)
                                .start(this@BackImageActivity,2)
                    }

        }


    }
    override fun onActivityResult(requestCode:Int , resultCode: Int, data: Intent?){
        if (resultCode == RESULT_OK&&data!=null) {
            val uri=UCrop.getOutput(data)
        defaultSharedPreferences.edit().putString("BackGroundUri",uri.toString()).apply()
            iv_background.setImageBitmap(null)
            Picasso.with(this@BackImageActivity).load(uri).into(iv_background)

        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}

