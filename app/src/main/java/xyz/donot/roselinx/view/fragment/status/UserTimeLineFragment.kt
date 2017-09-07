package xyz.donot.roselinx.view.fragment.status

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import com.klinker.android.link_builder.LinkBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.person_item.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Paging
import twitter4j.ResponseList
import twitter4j.Status
import twitter4j.User
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.*
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.util.getTagURLMention
import xyz.donot.roselinx.util.getURLLink
import xyz.donot.roselinx.view.activity.EditProfileActivity
import xyz.donot.roselinx.view.activity.PictureActivity
import xyz.donot.roselinx.view.activity.UserListActivity
import xyz.donot.roselinx.view.activity.UserListsActivity
import xyz.donot.roselinx.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselinx.view.custom.MyViewHolder
import java.text.SimpleDateFormat

class UserTimeLineFragment : TimeLineFragment() {
	override fun GetData(): ResponseList<Status>? = twitter.getUserTimeline(user.id, Paging(page))

	val user by lazy { arguments.getSerializable("user") as User }
	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		adapter.setHeaderView(setUpViews())
	}

	override fun pullToRefresh(adapter: MyBaseRecyclerAdapter<Status, MyViewHolder>) {
		launch(UI) {
			try {
				val result = twitter.getUserTimeline(Paging(adapter.data[0].id))
				insertDataBackground(result)
			} catch (e: Exception) {
				activity.twitterExceptionToast(e)
			}
		}
	}

	private fun setUpViews(): View =
		context.inflate(R.layout.person_item).apply {
		val iconIntent = Intent(activity, PictureActivity::class.java).putStringArrayListExtra("picture_urls", arrayListOf(user.originalProfileImageURLHttps))
		Picasso.with(activity).load(user.originalProfileImageURLHttps).into(iv_icon)
		iv_icon.setOnClickListener { startActivity(iconIntent) }
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
		if (user.isVerified || user.screenName == "JlowoIL") {
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

		tv_list.setOnClickListener {
			activity.start<UserListsActivity>(Bundle { putLong("userId", user.id) })
		}
		tv_friends.setOnClickListener {
			activity.start<UserListActivity>(Bundle {
				putLong("userId", user.id)
				putBoolean("isFriend", true)
			})
		}
		tv_follower.setOnClickListener {
			activity.start<UserListActivity>(Bundle {
				putLong("userId", user.id)
				putBoolean("isFriend", false)
			})
		}
		if (user.id != getMyId()) {
			tv_isfollowed.show()
			bt_follow.show()
			launch(UI) {
				try {
					val result = async(CommonPool) { twitter.showFriendship(getMyId(), user.id) }.await()
					bt_follow.isChecked = result.isSourceFollowingTarget
					bt_follow.setOnCheckedChangeListener { _, b ->
						if (b) {
							launch(UI) {
								async(CommonPool) { twitter.createFriendship(user.id) }.await()
								toast("フォローしました")
							}
						} else {
							launch(UI) {
								async(CommonPool) { twitter.destroyFriendship(user.id) }.await()
								toast("フォロー解除しました")
							}
						}
					}
					if (result.isTargetFollowingSource) {
						tv_isfollowed.setText(R.string.follows_you)
					} else {
						tv_isfollowed.setText(R.string.not_following_you)
					}
				} catch (e: Exception) {
					toast(e.localizedMessage)
				}

			}
		} else {
			bt_edit.show()
			bt_edit.setOnClickListener { activity.start<EditProfileActivity>() }
		}
		}




}
