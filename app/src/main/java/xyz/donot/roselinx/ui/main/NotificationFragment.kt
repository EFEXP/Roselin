package xyz.donot.roselinx.ui.main

import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_notification.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.R
import xyz.donot.roselinx.customrecycler.CalculableAdapter
import xyz.donot.roselinx.model.entity.NRETWEET
import xyz.donot.roselinx.model.entity.Notification
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.ui.base.ARecyclerFragment
import xyz.donot.roselinx.ui.detailtweet.TwitterDetailActivity
import xyz.donot.roselinx.ui.detailuser.UserActivity
import xyz.donot.roselinx.ui.status.KViewHolder
import xyz.donot.roselinx.ui.util.extraUtils.bundle
import xyz.donot.roselinx.ui.util.extraUtils.start
import xyz.donot.roselinx.ui.util.getExpandedText

class NotificationFragment : ARecyclerFragment() {
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = NotificationAdapter()
        recycler.adapter = adapter
        launch(UI) {
            async {
                RoselinDatabase.getInstance().notificationDao().getAllData()
                        .create(0, PagedList.Config.Builder().setPageSize(50).setPrefetchDistance(50).build()) }.await()
                    .observe(this@NotificationFragment, Observer {
                        it?.let {
                            adapter.setList(it)
                        }
                    })
        }
    }

    inner class NotificationAdapter: CalculableAdapter<Notification>(R.layout.item_notification) {
        override fun onBindViewHolder(holder: KViewHolder, position: Int) {
            getItem(position)?.let {
                val s = it.status
                val item = s.retweetedStatus ?: s
                val user = it.sourceUser
                holder.containerView.apply {
                    constraint2.setOnClickListener { activity.start<TwitterDetailActivity>(bundle  { putSerializable("Status", item) }) }
                    iv_notification_sender_icon.setOnClickListener { activity.start<UserActivity>(bundle  { putLong("user_id", user.id) }) }
                    tv_notification_myname.text = item.user.name
                    if (it.type == NRETWEET) {
                        tv_notification_info.text = getString(R.string.notification_retweet_message,user.name)
                        tv_notification_info.setCompoundDrawablesRelativeWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.wrap_retweet_pressed, null), null, null, null)
                    } else {
                        tv_notification_info.text = getString(R.string.notification_favorite_message,user.name)
                        tv_notification_info.setCompoundDrawablesRelativeWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.wrap_favorite_pressed, null), null, null, null)
                    }
                    tv_notification_text.text = getExpandedText(item)
                    Picasso.with(activity).load(user.originalProfileImageURLHttps).into(iv_notification_sender_icon)
                    Picasso.with(activity).load(item.user.originalProfileImageURLHttps).into(iv_notification_icon)
                    tv_notification_myscreen.text =getString(R.string.at_screenname,item.user.screenName)
                }
            }
        }

    }
}
