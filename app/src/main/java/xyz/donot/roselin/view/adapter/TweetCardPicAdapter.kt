package xyz.donot.roselin.view.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import xyz.donot.roselin.R
import xyz.donot.roselin.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselin.view.custom.MyViewHolder


class TweetCardPicAdapter(list: List<String>) : MyBaseRecyclerAdapter<String, MyViewHolder>(R.layout.item_bitmap, list) {
    override fun convert(helper:MyViewHolder, item: String) {


        Picasso.with(mContext).load(item).into(object : Target {
            override fun onBitmapFailed(errorDrawable: Drawable?) {

            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
               

            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

            }

        })
    }
}
