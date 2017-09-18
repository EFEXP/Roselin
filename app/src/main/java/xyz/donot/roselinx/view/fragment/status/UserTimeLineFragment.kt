package xyz.donot.roselinx.view.fragment.status

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Paging
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
import xyz.donot.roselinx.viewmodel.UserViewModel

class UserTimeLineFragment : TimeLineFragment() {
    private lateinit var myViewModel: UserViewModel
    val userId by lazy { arguments.getLong("userId") }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myViewModel = ViewModelProviders.of(activity).get(UserViewModel::class.java)
        viewmodel.pullToRefresh = { twitter ->
            async(CommonPool) { twitter.getUserTimeline(Paging(viewmodel.adapter.data[0].id)) }
        }
        myViewModel.mUser.observe(this, Observer {
            it?.let {
                viewmodel.adapter.setHeaderView(setUpViews(it))
            }
        })
        viewmodel.getData = { twitter ->
            async(CommonPool) {
                twitter.getUserTimeline(userId, Paging(viewmodel.page))
            }
        }
    }
    private fun setUpViews(user: User): View =
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
                editClick = { activity.start<EditProfileActivity>() }

                followClick = {
                    launch(UI) {
                        try {
                            async(CommonPool) { viewmodel.twitter.createFriendship(user.id) }.await()
                            toast("フォローしました")
                        } catch (e: Exception) {
                            toast(e.localizedMessage)
                        }
                    }
                }
                destroyFollowClick = {
                    try {
                        launch(UI) {
                            async(CommonPool) { viewmodel.twitter.destroyFriendship(user.id) }.await()
                            toast("フォロー解除しました")
                        }
                    } catch (e: Exception) {
                        toast(e.localizedMessage)
                    }
                }

                launch(UI) {
                    try {
                        val result = async(CommonPool) { viewmodel.twitter.showFriendship(getMyId(), user.id) }.await()
                        setRelation(result, getMyId() == user.id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


            }
}
