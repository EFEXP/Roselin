package xyz.donot.roselinx.view.fragment.status

import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Paging
import twitter4j.ResponseList
import twitter4j.Status
import twitter4j.User
import xyz.donot.roselinx.util.extraUtils.Bundle
import xyz.donot.roselinx.util.extraUtils.start
import xyz.donot.roselinx.util.extraUtils.toast
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.view.activity.EditProfileActivity
import xyz.donot.roselinx.view.activity.PictureActivity
import xyz.donot.roselinx.view.activity.UserListActivity
import xyz.donot.roselinx.view.activity.UserListsActivity
import xyz.donot.roselinx.view.custom.UserDetailView

class UserTimeLineFragment : TimeLineFragment() {
    override fun GetData(): ResponseList<Status>? = viewmodel.twitter.getUserTimeline(user.id, Paging(page))

    val user by lazy { arguments.getSerializable("user") as User }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.adapter.setHeaderView(setUpViews())
        viewmodel.pullToRefresh = { twitter ->
            async(CommonPool) { twitter.getUserTimeline(Paging(viewmodel.adapter.data[0].id)) }
        }
    }


    private fun setUpViews(): View =
            UserDetailView(activity).apply {
                val iconIntent = Intent(activity, PictureActivity::class.java).putStringArrayListExtra("picture_urls", arrayListOf(user.originalProfileImageURLHttps))
                setUser(user)
                iconClick = { startActivity(iconIntent) }
                listClick = { activity.start<UserListsActivity>(Bundle { putLong("userId", user.id) }) }
                friendClick = {
                    activity.start<UserListActivity>(Bundle {
                        putLong("userId", user.id)
                        putBoolean("isFriend", true)
                    })
                }
                followerClick = {
                    activity.start<UserListActivity>(Bundle {
                        putLong("userId", user.id)
                        putBoolean("isFriend", false)
                    })
                }
                editClick={ activity.start<EditProfileActivity>() }
                try {
                followClick={
                    launch(UI) {
                        async(CommonPool) { viewmodel.twitter.createFriendship(user.id) }.await()
                        toast("フォローしました")
                    }
                }
                destroyFollowClick={
                    launch(UI) {
                        async(CommonPool) { viewmodel.twitter.destroyFriendship(user.id) }.await()
                        toast("フォロー解除しました")
                    }
                }

                launch(UI) {

                        val result = async(CommonPool) { viewmodel.twitter.showFriendship(getMyId(), user.id) }.await()
                        setRelation(result, getMyId()==user.id) }
                   }
                catch (e:Exception){
                    toast(e.localizedMessage)
                }


            }


    /*
    *
    *
    *       val iconIntent = Intent(activity, PictureActivity::class.java).putStringArrayListExtra("picture_urls", arrayListOf(user.originalProfileImageURLHttps))
          Picasso.with(activity).load(user.originalProfileImageURLHttps).into(iv_icon)
          iv_icon.setOnClickListener { startActivity(iconIntent) }
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
                      val result = async(CommonPool) { viewmodel.twitter.showFriendship(getMyId(), user.id) }.await()
                      bt_follow.isChecked = result.isSourceFollowingTarget
                      bt_follow.setOnCheckedChangeListener { _, b ->
                          if (b) {
                              launch(UI) {
                                  async(CommonPool) { viewmodel.twitter.createFriendship(user.id) }.await()
                                  toast("フォローしました")
                              }
                          } else {
                              launch(UI) {
                                  async(CommonPool) { viewmodel.twitter.destroyFriendship(user.id) }.await()
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
    *
    *
    *
    *
    *
    *
    *
    * */


}
