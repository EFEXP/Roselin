package xyz.donot.quetzal.view.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_user_detail.*
import twitter4j.Relationship
import twitter4j.Twitter
import twitter4j.User
import xyz.donot.roselin.R
import xyz.donot.roselin.extend.SafeAsyncTask
import xyz.donot.roselin.model.realm.DBAccount
import xyz.donot.roselin.util.extraUtils.start
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.view.activity.EditProfileActivity
import xyz.donot.roselin.view.activity.PictureActivity
import java.text.SimpleDateFormat


class UserDetailFragment: Fragment()
{
    val twitter by lazy { getTwitterInstance() }
    val user by lazy {arguments.getSerializable("user") as User }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return  inflater.inflate(R.layout.fragment_user_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val iconIntent= Intent(activity, PictureActivity::class.java).putStringArrayListExtra("picture_urls",arrayListOf(user.originalProfileImageURLHttps))
        Picasso.with(activity).load(user.originalProfileImageURLHttps).into(icon_user)
        icon_user.setOnClickListener{startActivity(iconIntent)}
        user_name.text=user.name
        val verify= ResourcesCompat.getDrawable(activity.resources, R.drawable.ic_check_circle_black_18dp,null)
        if (user.isVerified){user_name.setCompoundDrawablesWithIntrinsicBounds(null,null,verify,null)}
        screen_name.text="@${user.screenName}"
        description.text=user.description.replace("\n","")
        web_txt.text=user.urlEntity.expandedURL
        geo_txt.text=user.location
        tweet_count.text="${user.statusesCount}回のツイート"
        created_at.text= "${SimpleDateFormat("yyyy/MM/dd").format(user.createdAt)}にTwitterを開始"
        listed.text="${user.listedCount}個のリストに追加されています"
        followed_text.text="Followers:${user.followersCount}"
        following_text.text="Friends:${user.friendsCount}"
        if(user.id!= getMyId()) {
            follow_button.visibility=View.VISIBLE
            relation.visibility=View.VISIBLE
            class RelationshipTask:SafeAsyncTask<Twitter,Relationship>(){
                override fun doTask(arg: Twitter): Relationship {
                      return  twitter.showFriendship(getMyId(),user.id)
                }

                override fun onSuccess(result: Relationship) {
                    follow_button.apply {
                        if (result.isSourceFollowingTarget) {
                            text = "フォロー中"
                            setOnClickListener {

                            }
                        } else {
                            setOnClickListener {

                            }
                        }
                    }

                    if(result.isTargetFollowingSource){
                        relation.text = "フォローされています"
                    }
                    else{
                        relation.text = "フォローされていません"
                    }
                }

                override fun onFailure(exception: Exception) {

                }
            }
            RelationshipTask().execute(twitter)
        }
        else{
            edit_profile.visibility=View.VISIBLE
         edit_profile.setOnClickListener{  activity.start<EditProfileActivity>()}
        }


    }

}

fun getMyId(): Long{
    Realm.getDefaultInstance().use {
        return  it.where(DBAccount::class.java).equalTo("isMain",true).findFirst().id
    }
}