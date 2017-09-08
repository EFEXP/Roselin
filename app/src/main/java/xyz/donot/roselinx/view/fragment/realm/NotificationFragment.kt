package xyz.donot.roselinx.view.fragment.realm

import android.os.Bundle
import android.support.text.emoji.widget.EmojiTextView
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import io.realm.*
import kotlinx.android.synthetic.main.item_notification.view.*
import twitter4j.Status
import twitter4j.User
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.realm.DBNotification
import xyz.donot.roselinx.model.realm.NRETWEET
import xyz.donot.roselinx.util.extraUtils.Bundle
import xyz.donot.roselinx.util.extraUtils.start
import xyz.donot.roselinx.util.getDeserialized
import xyz.donot.roselinx.util.getExpandedText
import xyz.donot.roselinx.view.activity.TwitterDetailActivity
import xyz.donot.roselinx.view.activity.UserActivity
import xyz.donot.roselinx.view.fragment.ARecyclerFragment

class NotificationFragment : ARecyclerFragment() {


    private var isBackground = false
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val query = Realm.getDefaultInstance().where(DBNotification::class.java).findAllSorted("date", Sort.DESCENDING)
        val adapter = NotificationAdapter(query)
        recycler.adapter = adapter
        query.addChangeListener(RealmChangeListener<RealmResults<DBNotification>> {
            itemInserted()
        })
    }

    private fun itemInserted() {
        if (!isBackground) {
            val positionIndex = (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            if (positionIndex == 0) {
                (recycler).scrollToPosition(0)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isBackground = false
        itemInserted()
    }

    override fun onStop() {
        super.onStop()
        isBackground = true
    }


    inner class NotificationAdapter(orderedRealmCollection: OrderedRealmCollection<DBNotification>) : RealmRecyclerViewAdapter<DBNotification, NotificationAdapter.ViewHolder>(orderedRealmCollection, true) {
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            getItem(position)?.let {
                val s = it.status?.getDeserialized<Status>()
                val item = s?.retweetedStatus ?: s
                val user = it.sourceUser?.getDeserialized<User>()
                if (user != null && item != null && s != null) {
                    holder.apply {
                        card.setOnClickListener { activity.start<TwitterDetailActivity>(Bundle { putSerializable("Status", item) }) }
                        sendericon.setOnClickListener { activity.start<UserActivity>(Bundle { putLong("user_id", user.id) }) }
                        name.text = item.user.name
                        if (it.type == NRETWEET) {
                            fromText.text = "${user.name}さんがあなたのツイートをリツイートしました"
                            fromText.setCompoundDrawablesRelativeWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.wrap_retweet_pressed, null), null, null, null)
                        } else {
                            fromText.text = "${user.name}さんがあなたのツイートをいいねしました"
                            fromText.setCompoundDrawablesRelativeWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.wrap_favorite_pressed, null), null, null, null)
                        }
                        text.text = getExpandedText(item)
                        Picasso.with(activity).load(user.originalProfileImageURLHttps).into(sendericon)
                        Picasso.with(activity).load(item.user.originalProfileImageURLHttps).into(icon)
                        screen.text = "@" + item.user.screenName
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
                ViewHolder(layoutInflater.inflate(R.layout.item_notification, parent, false))

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.tv_notification_myname
            val fromText: TextView = view.tv_notification_info
            val text: EmojiTextView = view.tv_notification_text
            val sendericon: RoundedImageView = view.iv_notification_sender_icon
            val icon: RoundedImageView = view.iv_notification_icon
            val screen: TextView = view.tv_notification_myscreen
            val card: CardView = view.cardView
        }


    }
}
