package xyz.donot.roselinx.view.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.View
import com.klinker.android.link_builder.LinkBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.person_item.view.*
import twitter4j.Relationship
import twitter4j.User
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.show
import xyz.donot.roselinx.util.extraUtils.toast
import xyz.donot.roselinx.util.getTagURLMention
import xyz.donot.roselinx.util.getURLLink
import java.text.SimpleDateFormat

class UserDetailView : ConstraintLayout,Target {
    constructor (context: Context, attributeSet: AttributeSet, defStyleAttr: Int ): super(context, attributeSet, defStyleAttr)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context) : super(context)

    var iconClick: () -> Unit = {}
    var friendClick: () -> Unit = {}
    var followerClick: () -> Unit = {}
    var listClick: () -> Unit = {}
    var destroyFollowClick: () -> Unit = {}
    var followClick: () -> Unit = {}
    var editClick: () -> Unit = {}
    lateinit var iconBitmap: Bitmap

    init {
        View.inflate(context, R.layout.person_item, this)
    }

    fun setRelation(relationship: Relationship?, isMe: Boolean) {
        if (isMe) {
            bt_edit.show()
        } else {
            relationship?.let {

                bt_follow.isEnabled = true
                tv_isfollowed.show()
                bt_follow.show()
                bt_follow.isChecked = relationship.isSourceFollowingTarget
                if (relationship.isTargetFollowingSource) {
                    tv_isfollowed.setText(R.string.follows_you)
                } else {
                    tv_isfollowed.setText(R.string.not_following_you)
                }
            }
            //変わった後のが流れてくる
            bt_follow.setOnCheckedChangeListener({ _, checked ->
                context.toast(checked.toString())
                if (checked)
                    followClick()
                else
                    destroyFollowClick()
            })

        }
    }

    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

    }

    override fun onBitmapFailed(errorDrawable: Drawable?) {


    }

    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
        iv_icon.setImageBitmap(bitmap)
        iconBitmap = bitmap
    }

    fun setUser(user: User) {

        Picasso.with(context).load(user.originalProfileImageURLHttps).into(this)
        tv_name.text = user.name
        tv_description.text = if (user.description.isNullOrEmpty()) " No Description" else user.description.replace("\n", "")
        tv_web.text = if (user.urlEntity.expandedURL.isEmpty()) " No Url" else user.urlEntity.expandedURL
        tv_geo.text = if (user.location.isEmpty()) " No Location" else user.location
        tv_date.text = "${SimpleDateFormat("yyyy/MM/dd").format(user.createdAt)}に開始"
        tv_follower.setText(user.followersCount.toString())
        tv_friends.setText(user.friendsCount.toString())
        tv_list.setText(user.listedCount.toString())
        tv_tweets.setText(user.statusesCount.toString())
        tv_fav.setText(user.favouritesCount.toString())
        //認証済み
        if (user.isVerified) {
            tv_name.setCompoundDrawablesWithIntrinsicBounds(null, null, ResourcesCompat.getDrawable(context.resources, R.drawable.wraped_verify, null), null)
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
        tv_list.setOnClickListener { listClick() }
        tv_friends.setOnClickListener { friendClick() }
        tv_follower.setOnClickListener { followerClick() }
        bt_edit.setOnClickListener { editClick() }


    }

}
