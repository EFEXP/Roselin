package xyz.donot.roselin.view.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.squareup.picasso.Picasso
import twitter4j.User
import xyz.donot.roselin.R


class UserListAdapter: BaseQuickAdapter<User, BaseViewHolder>(R.layout.item_user) {

    override fun convert(helper: BaseViewHolder, item: User) {
        helper.apply {
        Picasso.with(mContext).load(item.biggerProfileImageURLHttps).into(getView<ImageView>(R.id.iv_icon))
        setText(R.id.tv_username,item.name)
            setText(R.id.tv_screenname,"@"+item.screenName)
            setText(R.id.tv_description,item.description)
        }

    }
    override fun getItemId(position: Int): Long {
        if(position < data.size){
            return data[position].id
        }
        return super.getItemId(position)

    }
}

