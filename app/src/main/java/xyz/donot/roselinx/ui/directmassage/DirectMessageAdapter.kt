package xyz.donot.roselinx.ui.directmassage

import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_directmessage.view.*
import twitter4j.DirectMessage
import xyz.donot.roselinx.R
import xyz.donot.roselinx.ui.util.getAccount

class DirectMessageAdapter : BaseQuickAdapter<DirectMessage, BaseViewHolder>(R.layout.item_directmessage) {
	private val myId by lazy { getAccount().id}
	override fun convert(helper: BaseViewHolder, item: DirectMessage) {
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
