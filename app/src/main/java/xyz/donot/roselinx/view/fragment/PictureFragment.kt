package xyz.donot.roselinx.view.fragment


import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.davemorrissey.labs.subscaleview.ImageSource
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fragment_picture.view.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.onClick
import xyz.donot.roselinx.util.extraUtils.toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class PictureFragment : Fragment() {
    private val stringURL by lazy { arguments.getString("url") }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_picture, container, false)
        Picasso.with(activity).load(stringURL).into(object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit

            override fun onBitmapFailed(errorDrawable: Drawable?) = Unit

            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                v.iv_picture.setImage(ImageSource.bitmap(bitmap))
              val palette=  Palette.from(bitmap).generate()
                v.iv_picture.background= ColorDrawable(palette.getMutedColor(ContextCompat.getColor(context,R.color.material_background)))
            }
        })
        v.bt_download.onClick {
            Save(stringURL)
        }


        return v
    }

    //実際のセーブ処理
    private fun Save(stringURL: String) = Picasso.with(activity).load(stringURL).into(object : com.squareup.picasso.Target {
        override fun onBitmapFailed(p0: Drawable?) = Unit
        override fun onBitmapLoaded(p0: Bitmap, p1: Picasso.LoadedFrom?) {
            val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            try {
                val name = Date().time
                val attachName = File("$file/", "$name.jpg")
                FileOutputStream(attachName).use {
                    p0.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    it.flush()
                    toast("保存しました")
                }

                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.TITLE, "$file/$name.jpg")
                    put("_data", attachName.absolutePath)
                }
                activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }

        override fun onPrepareLoad(p0: Drawable?) = Unit

    })

}
