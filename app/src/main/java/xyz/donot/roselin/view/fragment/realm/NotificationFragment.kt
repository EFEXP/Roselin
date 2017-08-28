package xyz.donot.roselin.view.fragment.realm

import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.chad.library.adapter.base.BaseViewHolder
import com.squareup.picasso.Picasso
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.Status
import twitter4j.User
import xyz.donot.roselin.R
import xyz.donot.roselin.model.realm.DBNotification
import xyz.donot.roselin.model.realm.NRETWEET
import xyz.donot.roselin.util.extraUtils.Bundle
import xyz.donot.roselin.util.extraUtils.start
import xyz.donot.roselin.util.getDeserialized
import xyz.donot.roselin.util.getExpandedText
import xyz.donot.roselin.view.activity.TwitterDetailActivity
import xyz.donot.roselin.view.activity.UserActivity
import xyz.donot.roselin.view.custom.MyBaseAdapter

class NotificationFragment:AppCompatDialogFragment(){
    val realm: Realm by lazy { Realm.getDefaultInstance() }
    val adapter by lazy { NotificationAdapter(realm.where(DBNotification::class.java).findAllSorted("date",Sort.DESCENDING)) }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dividerItemDecoration = DividerItemDecoration( recycler.context,
                LinearLayoutManager(activity).orientation)
        recycler.addItemDecoration(dividerItemDecoration)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter=adapter
        refresh.isEnabled=false
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.content_base_fragment, container, false)

    inner class NotificationAdapter(orderedRealmCollection: OrderedRealmCollection<DBNotification>):MyBaseAdapter<DBNotification, BaseViewHolder>(orderedRealmCollection,R.layout.item_notification){
        override fun convert(helper: BaseViewHolder, item: DBNotification) {
            val status=item.status.getDeserialized<Status>()
            val user=item.sourceUser.getDeserialized<User>()
            helper.apply {
               val t= if (item.type== NRETWEET){"${user.name}さんがあなたのツイートをリツイートしました" }
                else{"${user.name}さんがあなたのツイートをいいねしました"}
                val icon=getView<ImageView>(R.id.iv_notification_icon)
                setText(R.id.tv_notification_info,t)
                setText(R.id.tv_notification_myname,status.user.name)
                setText(R.id.tv_notification_text, getExpandedText(status))
                setText(R.id.tv_notification_myscreen,"@"+status.user.screenName)
                Picasso.with(activity).load(user.biggerProfileImageURLHttps).into(icon)
                icon.setOnClickListener{activity.start<UserActivity>(Bundle { putLong("user_id",status.user.id)})}
                getView<View>(R.id.cardView).setOnClickListener{activity.start<TwitterDetailActivity>(Bundle { putSerializable("Status",status) })}
            }

        }


    }
}

