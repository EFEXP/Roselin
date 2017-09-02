package xyz.donot.roselin.view.adapter

import android.widget.ImageView
import com.squareup.picasso.Picasso
import twitter4j.User
import xyz.donot.roselin.R
import xyz.donot.roselin.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselin.view.custom.MyViewHolder


class UserListAdapter : MyBaseRecyclerAdapter<User, MyViewHolder>(R.layout.item_user) {

	override fun convert(helper: MyViewHolder, item: User, position: Int) {
		helper.apply {
			Picasso.with(mContext).load(item.biggerProfileImageURLHttps).into(getView<ImageView>(R.id.iv_icon))
			setText(R.id.tv_username, item.name)
			setText(R.id.tv_screenname, "@" + item.screenName)
			setText(R.id.tv_description, item.description)
		}

	}

	override fun getItemId(position: Int): Long {
		if (position < data.size) {
			return data[position].id
		}
		return super.getItemId(position)

	}
}

