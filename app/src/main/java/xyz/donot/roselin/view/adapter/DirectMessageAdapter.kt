package xyz.donot.roselin.view.adapter

import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_directmessage.view.*
import twitter4j.DirectMessage
import xyz.donot.roselin.R
import xyz.donot.roselin.util.getMyId
import xyz.donot.roselin.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselin.view.custom.MyViewHolder

class DirectMessageAdapter : MyBaseRecyclerAdapter<DirectMessage, MyViewHolder>(R.layout.item_directmessage) {
	private val myId = getMyId()
	override fun convert(helper: MyViewHolder, item: DirectMessage, position: Int) {
		helper.getView<ViewGroup>(R.id.item_directmessage_root).apply {
			tv_screenname.text="@${item.senderScreenName}から@${item.recipientScreenName}"
			tv_description.text = item.text
			if (item.recipientId == myId) {
				tv_sender_username.text = item.sender.name
				Picasso.with(mContext).load(item.sender.originalProfileImageURLHttps).into(iv_sender_icon)
			} else {
				tv_sender_username.text = item.recipient.name
				Picasso.with(mContext).load(item.sender.originalProfileImageURLHttps).into(iv_sender_icon)
			}


		}


	}
}
