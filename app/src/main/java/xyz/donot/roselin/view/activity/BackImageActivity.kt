package xyz.donot.roselin.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import kotlinx.android.synthetic.main.activity_back_image.*
import kotlinx.android.synthetic.main.content_back_image.*
import xyz.donot.quetzal.viewmodel.adapter.TwitterImageAdapter
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.defaultSharedPreferences
import java.io.File
import java.util.*

class BackImageActivity : AppCompatActivity() {

    var bannerUri: Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back_image)
        setSupportActionBar(toolbar)
        val mAdapter=TwitterImageAdapter()
        recycler_background.adapter=mAdapter



        fab.setOnClickListener { view ->
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
        defaultSharedPreferences.edit().putString("BackGroundUri",UCrop.getOutput(data)!!.toString()).apply()


        }
    }

}
