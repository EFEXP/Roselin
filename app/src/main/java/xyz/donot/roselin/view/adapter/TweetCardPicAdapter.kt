package xyz.donot.roselin.view.adapter

import android.widget.ImageView
import com.squareup.picasso.Picasso
import xyz.donot.roselin.R
import xyz.donot.roselin.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselin.view.custom.MyViewHolder


class TweetCardPicAdapter(list: List<String>) : MyBaseRecyclerAdapter<String, MyViewHolder>(R.layout.item_bitmap, list) {
    override fun convert(helper:MyViewHolder, item: String) {


        Picasso.with(mContext).load(item).into(helper.getView<ImageView>(R.id.imageview_picture))
    }
}
