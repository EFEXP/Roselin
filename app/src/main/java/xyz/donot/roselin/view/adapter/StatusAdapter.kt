package xyz.donot.roselin.view.adapter

import android.content.Context
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.squareup.picasso.Picasso
import twitter4j.Status
import xyz.donot.roselin.R


class StatusAdapter(val context: Context,list:List<Status>) : BaseQuickAdapter<Status, BaseViewHolder>(R.layout.item_tweet,list)
{


    override fun convert(helper: BaseViewHolder, item: Status) {
        helper.setText(R.id.textview_text,item.text)
        helper.setText(R.id.textview_username,item.user.name)
        Picasso.with(mContext).load(item.user.originalProfileImageURLHttps).into(helper.getView<ImageView>(R.id.imageview_icon))


    }


}

