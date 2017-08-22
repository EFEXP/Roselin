package xyz.donot.roselin.view.activity

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_picture.*
import kotlinx.android.synthetic.main.content_picture.*
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.view.adapter.PicturePagerAdapter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class PictureActivity : AppCompatActivity() {
   private val strings by lazy { intent.extras.getStringArrayList("picture_urls") }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)
        setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.pictures)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val pager=picture_view_pager
        pager.offscreenPageLimit=strings.count()
        val pagerAdapter= PicturePagerAdapter(supportFragmentManager,strings)
        pager.adapter = pagerAdapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_picture,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id) {
             R.id.save_it ->
             {
               val url=  strings[picture_view_pager.currentItem]
                 Save(url)

             } }
        return super.onOptionsItemSelected(item)
    }

    //実際のセーブ処理
    private fun Save(stringURL:String) = Picasso.with(this).load( stringURL).into(object :com.squareup.picasso.Target{
        override fun onBitmapFailed(p0: Drawable?) = Unit
        override fun onBitmapLoaded(p0: Bitmap, p1: Picasso.LoadedFrom?) {
            val file  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            try{
                val name= Date().time
                val attachName = File("$file/", "$name.jpg")
                FileOutputStream(attachName).use {
                    p0.compress(Bitmap.CompressFormat.JPEG,100,it)
                    it.flush()
                  toast("保存しました")
                }

                val values=  ContentValues().apply {
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.TITLE,"$file/$name.jpg")
                    put("_data",attachName.absolutePath )
                }
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
            catch(ex: IOException){
                ex.printStackTrace()
            }
        }
        override fun onPrepareLoad(p0: Drawable?) = Unit

    })

}
