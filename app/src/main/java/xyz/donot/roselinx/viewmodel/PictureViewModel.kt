package xyz.donot.roselinx.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Environment
import android.provider.MediaStore
import com.squareup.picasso.Picasso
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.util.extraUtils.toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class PictureViewModel(application: Application) : AndroidViewModel(application) {
    val dominantColor=MutableLiveData<Int>()
    val mutedColor=MutableLiveData<Int>()
    fun savePicture(string: String) {
        val app=getApplication<Roselin>()
        Picasso.with(app).load(string).into(object : com.squareup.picasso.Target {
            override fun onBitmapFailed(p0: Drawable?) = Unit
            override fun onBitmapLoaded(p0: Bitmap, p1: Picasso.LoadedFrom?) {
                val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                try {
                    val name = Date().time
                    val attachName = File("$file/", "$name.jpg")
                    FileOutputStream(attachName).use {
                        p0.compress(Bitmap.CompressFormat.JPEG, 100, it)
                        it.flush()
                       app.toast("保存しました")
                    }

                    val values = ContentValues().apply {
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        put(MediaStore.Images.Media.TITLE, "$file/$name.jpg")
                        put("_data", attachName.absolutePath)
                    }
                   app.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }

            override fun onPrepareLoad(p0: Drawable?) = Unit

        })
    }
}
