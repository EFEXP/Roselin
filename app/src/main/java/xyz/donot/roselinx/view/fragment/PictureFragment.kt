package xyz.donot.roselinx.view.fragment


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
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
import xyz.donot.roselinx.viewmodel.PictureViewModel


class PictureFragment : Fragment() {
    private val stringURL by lazy { arguments.getString("url") }
    lateinit var viewmodel :PictureViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_picture, container, false)
       viewmodel= ViewModelProviders.of(activity).get(PictureViewModel::class.java)
        viewmodel.mutedColor.observe(this, Observer {
            it?.let {
                view.iv_picture.background= ColorDrawable(it)
            }
        })
        Picasso.with(activity).load(stringURL).into(object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit

            override fun onBitmapFailed(errorDrawable: Drawable?) = Unit

            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                view.iv_picture.setImage(ImageSource.bitmap(bitmap))
              val palette=  Palette.from(bitmap).generate()
               viewmodel.mutedColor.value= palette.getMutedColor(ContextCompat.getColor(context,R.color.material_background))
                viewmodel.dominantColor.value= palette.getDominantColor(ContextCompat.getColor(context,R.color.material_background))
            }
        })
        view.bt_download.onClick {
           viewmodel.savePicture(stringURL)
        }
        return view
    }

}
