package xyz.donot.roselinx.view.adapter

import android.widget.ImageView
import com.squareup.picasso.Picasso
import xyz.donot.roselinx.R
import xyz.donot.roselinx.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselinx.view.custom.MyViewHolder


class TweetCardPicAdapter(list: List<String>, private val isVideo: Boolean) : MyBaseRecyclerAdapter<String, MyViewHolder>(R.layout.item_bitmap, list) {
	override fun convert(helper: MyViewHolder, item: String, position: Int) {
		helper.setText(R.id.picture_type, if (isVideo) "Video" else "${position + 1}/$itemCount")
		Picasso.with(mContext).load(item).into(helper.getView<ImageView>(R.id.imageview_picture))
	}
}
