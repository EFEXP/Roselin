package xyz.donot.roselinx.view.adapter

import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Picasso
import xyz.donot.roselinx.R
import xyz.donot.roselinx.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselinx.view.custom.MyViewHolder

class TwitterImageAdapter : MyBaseRecyclerAdapter<Uri, MyViewHolder>(R.layout.item_edit_tweet_pictures) {
	override fun convert(helper: MyViewHolder, item: Uri, position: Int) {
		helper.apply {
			Picasso.with(mContext).load(item).fit().into(getView<ImageView>(R.id.iv_picture_edit))
		}
	}
}

