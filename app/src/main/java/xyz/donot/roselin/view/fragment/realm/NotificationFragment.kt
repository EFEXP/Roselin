package xyz.donot.roselin.view.fragment.realm

import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.realm.*
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.android.synthetic.main.item_notification.view.*
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

class NotificationFragment:AppCompatDialogFragment(){



    private var isBackground=false
    private val dataStore=ArrayList<DBNotification>()
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val query= Realm.getDefaultInstance().where(DBNotification::class.java).findAllSorted("date",Sort.DESCENDING)
        val dividerItemDecoration = DividerItemDecoration( recycler.context,
                LinearLayoutManager(activity).orientation)
        recycler.addItemDecoration(dividerItemDecoration)
        recycler.layoutManager = LinearLayoutManager(activity)
        val adapter= NotificationAdater(query)
        recycler.adapter=adapter
        refresh.isEnabled=false
       query.addChangeListener(RealmChangeListener<RealmResults<DBNotification>> {
           itemInserted()
       })
    }
    private fun itemInserted(){
        if(!isBackground){
            val  positionIndex =  (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            if (positionIndex==0) {
                (recycler).scrollToPosition(0)
            }
    }}
    override fun onResume() {
        super.onResume()
        isBackground=false
        itemInserted()
    }

    override fun onStop() {
        super.onStop()
        isBackground=true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.content_base_fragment, container, false)


    inner class NotificationAdater(orderedRealmCollection: OrderedRealmCollection<DBNotification>):RealmRecyclerViewAdapter<DBNotification, NotificationAdater.ViewHolder>(orderedRealmCollection,true){
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item=getItem(position)
            item?.let {
            val status=item.status.getDeserialized<Status>()
            val user=item.sourceUser.getDeserialized<User>()
            holder.apply {
                card.setOnClickListener{activity.start<TwitterDetailActivity>(Bundle { putSerializable("Status",status) })}
                icon.setOnClickListener{activity.start<UserActivity>(Bundle { putLong("user_id",user.id)})}
                name.text=status.user.name
                if (item.type== NRETWEET){fromText.text="${user.name}さんがあなたのツイートをリツイートしました" }
                else{fromText.text="${user.name}さんがあなたのツイートをいいねしました"}
                text.text= getExpandedText(status)
                Picasso.with(activity).load(user.biggerProfileImageURLHttps).into(icon)
                screen.text="@"+status.user.screenName
            }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
                ViewHolder(layoutInflater.inflate(R.layout.item_notification,parent,false))
        inner  class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView =view.tv_notification_myname
            val fromText=view.tv_notification_info
            val text=view.tv_notification_text
            val icon=view.iv_notification_icon
            val screen=view.tv_notification_myscreen
            val card=view.cardView

        }


    }
}
