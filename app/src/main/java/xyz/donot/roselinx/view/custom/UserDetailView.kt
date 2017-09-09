package xyz.donot.roselinx.view.custom

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.klinker.android.link_builder.LinkBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.person_item.view.*
import twitter4j.Relationship
import twitter4j.User
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.show
import xyz.donot.roselinx.util.getTagURLMention
import xyz.donot.roselinx.util.getURLLink
import java.text.SimpleDateFormat
import kotlin.properties.Delegates

class UserDetailView(context:Context,attributeSet: AttributeSet?):ConstraintLayout(context,attributeSet){
    constructor(context:Context):this(context,null)

    var view by Delegates.notNull<View>()
    var iconClick :()->Unit={}
    var friendClick:()->Unit={}
    var followerClick :()->Unit={}
    var listClick :()->Unit={}
    var destroyFollowClick :()->Unit={}
    var followClick :()->Unit={}
    var editClick:()->Unit={}
    init { view=LayoutInflater.from(context).inflate(R.layout.person_item,this) }

    fun setRelation(relationship: Relationship?,isMe:Boolean){
        if (isMe){
            bt_edit.show()
        }
        else{
          relationship?.let {
              view.apply {
                  tv_isfollowed.show()
                  bt_follow.show()
                  bt_follow.isChecked = relationship.isSourceFollowingTarget
                  if (relationship.isTargetFollowingSource) {
                      tv_isfollowed.setText(R.string.follows_you)
                  } else {
                      tv_isfollowed.setText(R.string.not_following_you)
                  }
              }
          }
        }
    }
    fun setUser(user:User){
        view.apply {
            Picasso.with(context).load(user.originalProfileImageURLHttps).into(iv_icon)
            tv_name.text = user.name
            tv_description.text = if (user.description.isNullOrEmpty()) " No Description" else user.description.replace("\n", "")
            tv_web.text = if (user.urlEntity.expandedURL.isEmpty()) " No Url" else user.urlEntity.expandedURL
            tv_geo.text = if (user.location.isEmpty()) " No Location" else user.location
            tv_tweets.text = user.statusesCount.toString()
            tv_date.text = "${SimpleDateFormat("yyyy/MM/dd").format(user.createdAt)}に開始"
            tv_follower.text = user.followersCount.toString()
            tv_friends.text = user.friendsCount.toString()
            tv_fav.text = user.favouritesCount.toString()
            tv_list.text = user.listedCount.toString()
            //認証済み
            if (user.isVerified) {
                tv_name
                        .setCompoundDrawablesWithIntrinsicBounds(null, null, ResourcesCompat.getDrawable(context.resources, R.drawable.wraped_verify, null), null)
            } else {
                tv_name.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
            //鍵垢
            if (user.isProtected) {
                tv_date.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(context.resources, R.drawable.wrap_lock, null), null, null, null)
            } else {
                tv_date.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
            //Linkable
            LinkBuilder.on(tv_web).addLinks(context.getURLLink()).build()
            LinkBuilder.on(tv_description).addLinks(context.getTagURLMention()).build()
            iv_icon.setOnClickListener { iconClick() }
            tv_list.setOnClickListener{listClick()}
            tv_follower.setOnClickListener{ friendClick()}
            tv_friends.setOnClickListener{followerClick()}
        }

    }

}
