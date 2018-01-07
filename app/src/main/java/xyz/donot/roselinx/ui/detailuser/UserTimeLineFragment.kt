package xyz.donot.roselinx.ui.detailuser

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.User
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.model.entity.USER_TIMELINE
import xyz.donot.roselinx.ui.base.ARecyclerFragment
import xyz.donot.roselinx.ui.detailtweet.TwitterDetailActivity
import xyz.donot.roselinx.ui.dialog.RetweetUserDialog
import xyz.donot.roselinx.ui.editprofile.EditProfileActivity
import xyz.donot.roselinx.ui.editteweet.EditTweetActivity
import xyz.donot.roselinx.ui.picture.PictureActivity
import xyz.donot.roselinx.ui.status.TweetAdapter
import xyz.donot.roselinx.ui.userlist.UserListActivity
import xyz.donot.roselinx.ui.userslist.UsersListActivity
import xyz.donot.roselinx.ui.util.extraUtils.*
import xyz.donot.roselinx.ui.util.getAccount
import xyz.donot.roselinx.ui.util.getDragdismiss
import xyz.donot.roselinx.ui.view.UserDetailView


class UserTimeLineFragment : ARecyclerFragment() {
    val viewmodel: UserTimeLineViewModel by lazy { ViewModelProviders.of(activity!!).get(UserTimeLineViewModel::class.java) }
    val adapter by lazy { TweetAdapter(activity!!) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.twitter = getAccount()
        viewmodel.dataRefreshed.observe(this@UserTimeLineFragment, Observer {
            refresh.setRefreshing(false)
        })
        recycler.adapter = adapter
        viewmodel.mUserID.observe(this, Observer { userId ->
            userId?.let {
                launch(UI) {
                    async {
                        RoselinDatabase.getInstance().tweetDao().getAllUserDataSource(USER_TIMELINE,viewmodel.twitter.id,arguments!!.getLong("userId"))
                                .create(0, PagedList.Config.Builder().setPageSize(50).setPrefetchDistance(50).build())
                    }.await().observe(this@UserTimeLineFragment, Observer {
                                it?.let {
                                    if (it.isEmpty()) {
                                        viewmodel.loadMoreData(false, userId)
                                    }
                                    adapter.setList(it)
                                }
                            })
                }

                viewmodel.mUser.observe(this@UserTimeLineFragment, Observer {
                    it?.let {
                        u->
                       // Handler().post {    adapter.setHeaderView(setUpViews(u))}
                    }
                })
                adapter.onItemClick = { (status), _ ->
                    val item = if (status.isRetweet) {
                        status.retweetedStatus
                    } else {
                        status
                    }
                    if (!activity!!.isFinishing) {
                        val tweetItem = if (viewmodel.mainTwitter.id == status.user.id) {
                            R.array.tweet_my_menu
                        } else {
                            R.array.tweet_menu
                        }
                        AlertDialog.Builder(context!!).setItems(tweetItem, { _, int ->
                            val selectedItem = context!!.resources.getStringArray(tweetItem)[int]
                            when (selectedItem) {
                                "返信" -> {
                                    startActivity(EditTweetActivity.newIntent(activity!!,item.text,item.id, item.user.screenName))
                                }
                                "削除" -> {
                                    launch(UI) {
                                        try {
                                            async(CommonPool) { viewmodel.mainTwitter.account.destroyStatus(status.id) }.await()
                                            toast("削除しました")
                                        } catch (e: Exception) {
                                            toast(e.localizedMessage)
                                        }
                                    }

                                }
                                "会話" -> {
                                    context!!.startActivity(context!!.newIntent<TwitterDetailActivity>(Bundle().apply { putSerializable("Status", item) }))

                                }
                                "コピー" -> {
                                    (context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip = ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_URILIST, item.text)
                                    toast("コピーしました")

                                }
                                "RTした人" -> {
                                    RetweetUserDialog.getInstance(item.id).show(childFragmentManager, "")
                                }
                                "共有" -> {
                                    context!!.startActivity(Intent().apply {
                                        action = Intent.ACTION_SEND
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, "@${item.user.screenName}さんのツイート https://twitter.com/${item.user.screenName}/status/${item.id}をチェック")
                                    })
                                }
                                "公式で見る" -> {
                                    CustomTabsIntent.Builder()
                                            .setShowTitle(true)
                                            .addDefaultShareMenuItem()
                                            .setToolbarColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
                                            .setStartAnimations(context!!, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                            .setExitAnimations(context!!, android.R.anim.slide_in_left, android.R.anim.slide_out_right).build()
                                            .launchUrl(context, Uri.parse("https://twitter.com/${item.user.screenName}/status/${item.id}"))
                                }
                            }
                        }).show()
                    }
                }
                adapter.onLoadMore = { viewmodel.loadMoreData(true, userId) }
                refresh.setOnRefreshListener {
                    Handler().delayed(1000, {
                        viewmodel.pullDown(userId)
                    })
                }

            }
        })
        viewmodel.mUserID.value = arguments!!.getLong("userId")
        refresh.isEnabled = true
    }

    private fun setUpViews(user: User): View {
        return UserDetailView(activity!!)
                .apply {
                    val iconIntent = activity!!.getDragdismiss(PictureActivity.createIntent(activity!!, arrayListOf(user.originalProfileImageURLHttps)))
                    setUser(user)
                    iconClick = { startActivity(iconIntent) }
                    listClick = { activity!!.start<UsersListActivity>(bundle { putLong("userId", user.id) }) }
                    friendClick = {
                        startActivity(UserListActivity.newIntent(activity!!,true,user.id))
                    }
                    followerClick = {
                       startActivity(UserListActivity.newIntent(activity!!,false,user.id))
                    }
                    editClick = { activity!!.start<EditProfileActivity>() }

                    followClick = {
                        launch(UI) {
                            try {
                                async(CommonPool) { viewmodel.twitter.account.createFriendship(user.id) }.await()
                                toast("フォローしました")
                            } catch (e: Exception) {
                                toast(e.localizedMessage)
                            }
                        }
                    }
                    destroyFollowClick = {
                        try {
                            launch(UI) {
                                async(CommonPool) { viewmodel.twitter.account.destroyFriendship(user.id) }.await()
                                toast("フォロー解除しました")
                            }
                        } catch (e: Exception) {
                            toast(e.localizedMessage)
                        }
                    }

                    launch(UI) {
                        try {
                            val result = async(CommonPool) { viewmodel.twitter.account.showFriendship(viewmodel.twitter.id, user.id) }.await()
                            setRelation(result, viewmodel.twitter.id == user.id)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }


                }
    }

    companion object {
        fun newInstance(userId: Long): UserTimeLineFragment {
            return UserTimeLineFragment().apply {
                arguments = bundle { putLong("userId", userId) }
            }
        }

    }
}

