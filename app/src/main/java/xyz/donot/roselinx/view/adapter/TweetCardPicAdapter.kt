package xyz.donot.roselinx.view.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_bitmap.view.*
import xyz.donot.roselinx.R


class TweetCardPicAdapter(list: List<String>, private val isVideo: Boolean) : BaseQuickAdapter<String,BaseViewHolder>(R.layout.item_bitmap, list) {
    override fun convert(helper:BaseViewHolder, item: String) {
        helper.itemView.picture_type.text = if (isVideo) "Video" else "${helper.layoutPosition+1}/$itemCount"
        Picasso.with(mContext).load(item).into(helper.getView<ImageView>(R.id.imageview_picture))
    }
}
