package xyz.donot.roselinx.view.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_bitmap.view.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.hide
import xyz.donot.roselinx.util.extraUtils.show


class TweetCardPicAdapter(list: List<String>, private val isVideo: Boolean) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_bitmap, list) {
    override fun convert(helper: BaseViewHolder, item: String) {
        if (!isVideo) {
            helper.itemView.picture_type.text = "${helper.layoutPosition + 1}/$itemCount"
            helper.itemView.iv_play_video.hide()
        } else
            helper.itemView.iv_play_video.show()
        Picasso.with(mContext).load(item).into(helper.getView<ImageView>(R.id.imageview_picture))
    }
}
