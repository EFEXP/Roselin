package xyz.donot.roselin.view.adapter

import android.net.Uri
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.squareup.picasso.Picasso
import xyz.donot.roselin.R

class TwitterImageAdapter : BaseQuickAdapter<Uri, BaseViewHolder>(R.layout.item_edit_tweet_pictures){
    override fun convert(helper: BaseViewHolder, item: Uri) {
        helper.apply {
            Picasso.with(mContext).load(item).into( getView<ImageView>(R.id.iv_picture_edit))

        }
    }
}

