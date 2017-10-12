package xyz.donot.roselinx.ui.detailuser

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.model.entity.CustomProfile
import xyz.donot.roselinx.model.entity.MuteFilter
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.model.entity.UserData
import xyz.donot.roselinx.ui.editprofile.EditProfileActivity
import xyz.donot.roselinx.ui.editteweet.EditTweetActivity
import xyz.donot.roselinx.util.extraUtils.*
import xyz.donot.roselinx.ui.util.getAccount
import xyz.donot.roselinx.ui.util.getDragdismiss
import xyz.donot.roselinx.ui.status.StatusAdapter
import xyz.donot.roselinx.ui.view.MyLoadingView
import xyz.donot.roselinx.ui.view.SingleLiveEvent
import xyz.donot.roselinx.ui.view.UserDetailView
import xyz.donot.roselinx.ui.base.ARecyclerFragment
import xyz.donot.roselinx.ui.detailtweet.TwitterDetailActivity
import xyz.donot.roselinx.ui.picture.PictureActivity
import xyz.donot.roselinx.ui.userslist.UsersListActivity
import xyz.donot.roselinx.ui.dialog.RetweetUserDialog
import xyz.donot.roselinx.ui.userlist.UserListActivity
import kotlin.properties.Delegates

class UserTimeLineFragment : ARecyclerFragment() {
    val viewmodel: UserTimeLineViewModel by lazy { ViewModelProviders.of(activity).get(UserTimeLineViewModel::class.java) }
    val userId by lazy { arguments.getLong("userId") }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.apply {
            twitter= getAccount().account
                adapter.apply {
                    setOnItemClickListener{ adapter, _, position ->
                        val status = adapter.data[position] as Status
                        val item = if (status.isRetweet) {
                            status.retweetedStatus
                        } else {
                            status
                        }
                        if (!activity.isFinishing) {
                            val tweetItem = if (mainTwitter.id == status.user.id) {
                                R.array.tweet_my_menu
                            } else {
                                R.array.tweet_menu
                            }
                            AlertDialog.Builder(context).setItems(tweetItem, { _, int ->
                                val selectedItem = context.resources.getStringArray(tweetItem)[int]
                                when (selectedItem) {
                                    "返信" -> {
                                        Bundle {
                                            putString("status_txt", item.text)
                                            putLong("status_id", item.id)
                                            putString("user_screen_name", item.user.screenName)
                                        }
                                        activity.start<EditTweetActivity>(
                                                Bundle {
                                                    putString("status_txt", item.text)
                                                    putLong("status_id", item.id)
                                                    putString("user_screen_name", item.user.screenName)
                                                }
                                        )
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
                                        context.startActivity(context.newIntent<TwitterDetailActivity>(Bundle().apply { putSerializable("Status", item) }))

                                    }
                                    "コピー" -> {
                                        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip = ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_URILIST, item.text)
                                        toast("コピーしました")

                                    }
                                    "RTした人" -> {
                                        RetweetUserDialog.getInstance(item.id).show(childFragmentManager, "")
                                    }
                                    "共有" -> {
                                        context.startActivity(Intent().apply {
                                            action = Intent.ACTION_SEND
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, "@${item.user.screenName}さんのツイート https://twitter.com/${item.user.screenName}/status/${item.id}をチェック")
                                        })
                                    }
                                    "公式で見る" -> {
                                        CustomTabsIntent.Builder()
                                                .setShowTitle(true)
                                                .addDefaultShareMenuItem()
                                                .setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
                                                .setStartAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                                .setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right).build()
                                                .launchUrl(context, Uri.parse("https://twitter.com/${item.user.screenName}/status/${item.id}"))
                                    }
                                }
                            }).show()
                        }}

                    exception.observe(this@UserTimeLineFragment , Observer {
                        it?.let {
                            adapter.emptyView = View.inflate(activity, R.layout.item_no_content, null)
                        }
                    })
                    dataRefreshed.observe(this@UserTimeLineFragment, Observer {
                        refresh.setRefreshing(false)
                    })
                    dataInserted.observe(this@UserTimeLineFragment, Observer {
                        val positionIndex = (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                        if (positionIndex == 0) {
                            recycler.layoutManager.scrollToPosition(0)
                        }
                    })


                    setOnLoadMoreListener({ viewmodel.loadMoreData(userId) }, recycler)
                   setLoadMoreView(MyLoadingView())
                   emptyView = View.inflate(activity, R.layout.item_empty, null)
                }
            if (savedInstanceState == null)
                loadMoreData(userId)
            mUser.observe(this@UserTimeLineFragment, Observer {
                it?.let {
                    viewmodel.adapter.setHeaderView(setUpViews(it))
                }
            })
            recycler.adapter = adapter
            refresh.setOnRefreshListener {
                Handler().delayed(1000, {
                    pullDown()
                })
            }
        }
        refresh.isEnabled = true
    }
    override fun onResume() {
        super.onResume()
        viewmodel.isBackground = false
    }

    override fun onStop() {
        super.onStop()
        viewmodel.isBackground = true
    }
    private fun setUpViews(user: User): View =
            UserDetailView(activity).apply {
                val iconIntent =activity.getDragdismiss(PictureActivity.createIntent(activity,arrayListOf(user.originalProfileImageURLHttps)))
                setUser(user)
                iconClick = { startActivity(iconIntent) }
                listClick = { activity.start<UsersListActivity>(Bundle { putLong("userId", user.id) }) }
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
                        val result = async(CommonPool) { viewmodel.twitter.showFriendship(viewmodel.twitter.id, user.id) }.await()
                        setRelation(result, viewmodel.twitter.id == user.id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


            }
    companion object {
        fun newInstance(userId:Long): UserTimeLineFragment {
         return UserTimeLineFragment().apply {
             arguments=xyz.donot.roselinx.util.extraUtils.Bundle {  putLong("userId",userId) }
           }
        }

    }
}

class UserTimeLineViewModel(app: Application) : AndroidViewModel(app) {
    val adapter by lazy { StatusAdapter() }
    var twitter by Delegates.notNull<Twitter>()
    val dataRefreshed = SingleLiveEvent<Unit>()
    val mainTwitter by lazy { getAccount() }
    val exception = MutableLiveData<TwitterException>()
    val dataInserted = SingleLiveEvent<Unit>()
    private val dataStore: ArrayList<Status> = ArrayList()
    var page: Int = 0
        get() {
            field++
            return field
        }
    var mUser: MutableLiveData<User> = MutableLiveData()
    fun insertDataBackground(data: List<Status>) = mainThread {
        mainThread {
            if (isBackground) {
                dataStore.addAll(0, data)
            } else {
                adapter.addData(0, data)
                dataInserted.call()
            }
        }
    }

    fun endAdapter() = mainThread {
        adapter.loadMoreEnd(true)
    }
    var isBackground = false
        set(value) {
            if (!value)
                if (dataStore.isNotEmpty()) {
                    adapter.addData(0, dataStore)
                    dataStore.clear()
                    dataInserted.call()
                }
        }

    fun insertDataBackground(data: Status) = mainThread {
        mainThread {
            if (isBackground) {
                dataStore.add(0, data)
            } else {
                adapter.addData(0, data)
                dataInserted.call()
            }
        }
    }



    fun loadMoreData(userID:Long) {
        launch(UI) {
            try {
                val result = async(CommonPool) { twitter.getUserTimeline(userID,Paging(page)) }.await()
                if (result.isEmpty()) {
                 endAdapter()
                } else {
                    adapter.addData(result)
                    adapter.loadMoreComplete()
                }
            } catch (e: TwitterException) {
                adapter.loadMoreFail()
                exception.value = e
                getApplication<Roselin>().toast(twitterExceptionMessage(e))
            }
        }
    }
    fun pullDown() {
        if (adapter.data.isNotEmpty()) {
            launch(UI) {
                async(CommonPool) { twitter.getUserTimeline(Paging(adapter.data[0].id)) }.await()?.let { insertDataBackground(it) }
                dataRefreshed.call()
            }
        } else {
            dataRefreshed.call()
        }
    }


    fun initUser(screenName: String) {
        if (mUser.value == null) {
            // val user=    realm.where(UserObject::class.java).equalTo("screenname",screenName).findFirst()
            //  user?.let {
            //      mUser.value=user.user.getDeserialized()
            //   }?:
            launch(UI) {
                try {
                    val result= async(CommonPool) { getAccount().account.showUser(screenName) }.await()
                    mUser.value =result
                    UserData.save(UserData(user = result,id = result.id,screenname = result.screenName))
                } catch (e: TwitterException) {
                    val user=  RoselinDatabase.getInstance().userDataDao().findByScreenName(screenName)
                    mUser.value =user.user
                    getApplication<Roselin>().toast(twitterExceptionMessage(e))
                }
            }
        }
    }
    fun initUser(id:Long) {
        if (mUser.value == null) {
            //  val user=    realm.where(UserObject::class.java).equalTo("tweetId",tweetId).findFirst()
            //    user?.let {
            //       mUser.value=user.user.getDeserialized()
            //   }?:
            launch(UI) {
                try {
                    val result= async(CommonPool) { getAccount().account.showUser(id) }.await()
                    mUser.value =result
                    UserData.save(UserData(user = result,id = result.id,screenname = result.screenName))
                } catch (e: TwitterException) {
                    val user=    RoselinDatabase.getInstance().userDataDao().findById(id)
                    mUser.value =user.user
                    getApplication<Roselin>().toast(twitterExceptionMessage(e))
                }
            }
        }
    }

    fun muteUser() {
        launch(UI) {
            async {
                MuteFilter.save(MuteFilter(accountId =mUser.value!!.id,user = mUser.value))
            }.await()
            getApplication<Roselin>().toast("ミュートしました")
        }
    }



    fun changeName(string: String) {
        CustomProfile.save(CustomProfile(string,mUser.value!!.id))
        getApplication<Roselin>().toast("変更しました")
    }

    fun revertName(){
        CustomProfile.save(CustomProfile(null,mUser.value!!.id))
        getApplication<Roselin>().toast("戻しました")
    }


}