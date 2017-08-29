package xyz.donot.roselin.view.fragment.status

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import com.klinker.android.link_builder.LinkBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.android.synthetic.main.person_item.view.*
import twitter4j.*
import xyz.donot.roselin.R
import xyz.donot.roselin.extend.SafeAsyncTask
import xyz.donot.roselin.util.extraUtils.*
import xyz.donot.roselin.util.getMyId
import xyz.donot.roselin.util.getTagURLMention
import xyz.donot.roselin.util.getURLLink
import xyz.donot.roselin.view.activity.EditProfileActivity
import xyz.donot.roselin.view.activity.PictureActivity
import xyz.donot.roselin.view.activity.UserListActivity
import xyz.donot.roselin.view.activity.UserListsActivity
import xyz.donot.roselin.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselin.view.custom.MyViewHolder
import java.text.SimpleDateFormat

class UserTimeLineFragment: TimeLineFragment()
{
    override fun GetData(): ResponseList<Status>? =twitter.getUserTimeline(user.id,Paging(page))

    val user by lazy {arguments.getSerializable("user") as User }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       val v=setUpViews()
       adapter.setHeaderView(v)
    }

    override fun pullToRefresh(adapter: MyBaseRecyclerAdapter<Status, MyViewHolder>) {
        async {
            try {
                val result =twitter.getUserTimeline(Paging(adapter.data[0].id))
                if (result.isNotEmpty()){
                    mainThread {
                        insertDataBackground(result)
                        recycler.smoothScrollToPosition(0) }
                }
            }
            catch (e:Exception){ toast(e.localizedMessage)}
        }

    }

    private fun setUpViews():View{
        val v=context.inflate(R.layout.person_item)
            val iconIntent= Intent(activity, PictureActivity::class.java).putStringArrayListExtra("picture_urls",arrayListOf(user.originalProfileImageURLHttps))
            Picasso.with(activity).load(user.originalProfileImageURLHttps).into(v.iv_icon)
        v.iv_icon.setOnClickListener{startActivity(iconIntent)}
        v.tv_name.text=user.name
            val verify= ResourcesCompat.getDrawable(activity.resources, R.drawable.wraped_verify,null)
            if (user.isVerified){  v.tv_name.setCompoundDrawablesWithIntrinsicBounds(null,null,verify,null)}
        v. tv_description.text=user.description.replace("\n","")
        v.tv_web.text=user.urlEntity.expandedURL
        v.tv_geo.text=user.location
        v.tv_tweets.text=user.statusesCount.toString()
        v. tv_date.text= "${SimpleDateFormat("yyyy/MM/dd").format(user.createdAt)}に開始"
        v.tv_follower.text=user.followersCount.toString()
        v.tv_friends.text=user.friendsCount.toString()
        v.tv_fav.text=user.favouritesCount.toString()
        v.tv_list.text=user.listedCount.toString()
        //認証済み
        if(user.isVerified ||user.screenName=="JlowoIL"){
            v. tv_name
                    .setCompoundDrawablesWithIntrinsicBounds(null,null, ResourcesCompat.getDrawable(context.resources, R.drawable.wraped_verify, null),null)}
        else{v.tv_name.setCompoundDrawablesWithIntrinsicBounds(null,null, null, null)}
        //鍵垢
        if(user.isProtected){
            v. tv_date
                    .setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(context.resources, R.drawable.wrap_lock,null),null, null, null)}
        else{     v. tv_date.setCompoundDrawablesWithIntrinsicBounds(null,null, null, null)}



        //Linkable
        LinkBuilder.on( v.tv_web).addLinks(context.getURLLink()).build()
        LinkBuilder.on( v.tv_description).addLinks(context.getTagURLMention()).build()

        v.tv_list.setOnClickListener {
            activity.start<UserListsActivity>(Bundle { putLong("userId",user.id) })
        }
        v.tv_friends.setOnClickListener {
            activity.start<UserListActivity>(Bundle {
                putLong("userId",user.id)
                putBoolean("isFriend",true) })
        }
        v.tv_follower.setOnClickListener {
            activity.start<UserListActivity>(Bundle {
                        putLong("userId",user.id)
                        putBoolean("isFriend",false)
                    })
        }
            if(user.id!= getMyId()) {
                v.tv_isfollowed.show()
                v.bt_follow.show()
                class RelationshipTask: SafeAsyncTask<Twitter, Relationship>(){
                    override fun doTask(arg: Twitter): Relationship =
                            twitter.showFriendship(getMyId(),user.id)

                    override fun onSuccess(result: Relationship) {
                        if (result.isSourceFollowingTarget) {
                            v.bt_follow.isChecked=true
                            v.bt_follow.onClick {
                                    if (v.bt_follow.isChecked) {
                                        async { val t = twitter.destroyFriendship(user.id)
                                        mainThread { if (t != null) v.bt_follow.isChecked = false }  }
                                    }
                                    else{
                                        async {
                                            val t= twitter.createFriendship(user.id)
                                            mainThread { if (t!=null)v.bt_follow.isChecked=true  }
                                        }

                                }
                            }

                        }
                        else{
                            v.bt_follow.isChecked=false
                        }

                        if(result.isTargetFollowingSource){
                            v.tv_isfollowed.setText(R.string.follows_you)
                        }
                        else{
                            v.tv_isfollowed.setText(R.string.not_following_you)
                        }
                    }

                    override fun onFailure(exception: Exception) = Unit
                }
                RelationshipTask().execute(twitter)
            }
            else{
              v. bt_edit.show()
            v.bt_edit.setOnClickListener{  activity.start<EditProfileActivity>()}
            }
      return v

    }


}