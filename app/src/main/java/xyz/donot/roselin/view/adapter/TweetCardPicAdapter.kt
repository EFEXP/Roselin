package xyz.donot.roselin.view.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.squareup.picasso.Picasso
import xyz.donot.roselin.R

class TweetCardPicAdapter(list: List<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_bitmap, list) {
    override fun convert(helper: BaseViewHolder, item: String) = Picasso.with(mContext).load(item).into(helper.getView<ImageView>(R.id.imageview_picture))
}
