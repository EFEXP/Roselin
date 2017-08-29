package xyz.donot.roselin.view.adapter

import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Picasso
import xyz.donot.roselin.R
import xyz.donot.roselin.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselin.view.custom.MyViewHolder

class TwitterImageAdapter : MyBaseRecyclerAdapter<Uri, MyViewHolder>(R.layout.item_edit_tweet_pictures){
    override fun convert(helper:MyViewHolder, item: Uri) {
        helper.apply {
            Picasso.with(mContext).load(item).resize(500,300).centerCrop().into( getView<ImageView>(R.id.iv_picture_edit))
        }
    }
}

