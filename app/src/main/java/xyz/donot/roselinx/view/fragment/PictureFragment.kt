package xyz.donot.roselinx.view.fragment


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.graphics.Palette
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.davemorrissey.labs.subscaleview.ImageSource
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fragment_picture.view.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.logd
import xyz.donot.roselinx.viewmodel.PictureViewModel


class PictureFragment : Fragment() {
    private val page by lazy { arguments.getInt("page") }
    lateinit var viewmodel: PictureViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_picture, container, false)
        viewmodel = ViewModelProviders.of(activity).get(PictureViewModel::class.java)
        viewmodel.urlList.observe(this, Observer {
            it?.let {
                logd { it[page] }
                Picasso.with(activity).load(it[page]).into(object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit
                    override fun onBitmapFailed(errorDrawable: Drawable?) = Unit
                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                        view.iv_picture.setImage(ImageSource.bitmap(bitmap))
                        Palette.from(bitmap).generate(
                                {
                                    viewmodel.mutedColor.value = it.lightMutedSwatch
                                }
                        )

                    }
                })
            }
        })
        return view
    }

}
