package xyz.donot.roselinx.view.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_user.view.*
import twitter4j.User
import xyz.donot.roselinx.R


class UserListAdapter : BaseQuickAdapter<User, BaseViewHolder>(R.layout.item_user) {

    override fun convert(helper:BaseViewHolder, item: User) {
        helper.apply {
            Picasso.with(mContext).load(item.biggerProfileImageURLHttps).into(getView<ImageView>(R.id.iv_icon))
            helper.itemView.apply {
                tv_username.text=item.name
                tv_screenname.text= "@" + item.screenName
                tv_description.text= item.description
            }

        }
    }

    override fun getItemId(position: Int): Long {
        if (position < data.size) {
            return data[position].id
        }
        return super.getItemId(position)

    }
}

