package xyz.donot.roselinx.view.fragment.status

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.view.View
import io.realm.Realm
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Paging
import twitter4j.TwitterException
import twitter4j.User
import xyz.donot.roselinx.R
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.model.realm.CustomProfileObject
import xyz.donot.roselinx.model.realm.MuteObject
import xyz.donot.roselinx.model.room.RoselinDatabase
import xyz.donot.roselinx.model.room.UserData
import xyz.donot.roselinx.util.extraUtils.*
import xyz.donot.roselinx.util.getDragdismiss
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.util.getSerialized
import xyz.donot.roselinx.util.getTwitterInstance
import xyz.donot.roselinx.view.activity.EditProfileActivity
import xyz.donot.roselinx.view.activity.PictureActivity
import xyz.donot.roselinx.view.activity.UserListActivity
import xyz.donot.roselinx.view.activity.UsersListActivity
import xyz.donot.roselinx.view.custom.MyLoadingView
import xyz.donot.roselinx.view.custom.UserDetailView
import xyz.donot.roselinx.view.fragment.base.MainTimeLineFragment
import xyz.donot.roselinx.view.fragment.base.MainTimeLineViewModel

class UserTimeLineFragment : MainTimeLineFragment() {
    override val viewmodel: UserTimeLineViewModel by lazy { ViewModelProviders.of(activity).get(UserTimeLineViewModel::class.java) }
    val userId by lazy { arguments.getLong("userId") }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.apply {
            twitter= getTwitterInstance()
                adapter.apply {
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
                        val result = async(CommonPool) { viewmodel.twitter.showFriendship(getMyId(), user.id) }.await()
                        setRelation(result, getMyId() == user.id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


            }
    companion object {
        fun newInstance(userId:Long):UserTimeLineFragment{
         return UserTimeLineFragment().apply {
             arguments=xyz.donot.roselinx.util.extraUtils.Bundle {  putLong("userId",userId) }
           }
        }

    }
}

class UserTimeLineViewModel(app: Application) : MainTimeLineViewModel(app) {
    var mUser: MutableLiveData<User> = MutableLiveData()
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
                    val result= async(CommonPool) { getTwitterInstance().showUser(screenName) }.await()
                    mUser.value =result
                    UserData.save(getApplication<Roselin>(),result)
                } catch (e: TwitterException) {
                    val user=  RoselinDatabase.getInstance(getApplication()).userDataDao().findByScreenName(screenName)
                    mUser.value =user.user
                    getApplication<Roselin>().toast(twitterExceptionMessage(e))
                }
            }
        }
    }
    fun initUser(id:Long) {
        if (mUser.value == null) {
            //  val user=    realm.where(UserObject::class.java).equalTo("id",id).findFirst()
            //    user?.let {
            //       mUser.value=user.user.getDeserialized()
            //   }?:
            launch(UI) {
                try {
                    val result= async(CommonPool) { getTwitterInstance().showUser(id) }.await()
                    mUser.value =result
                    UserData.save(getApplication<Roselin>(),result)
                } catch (e: TwitterException) {
                    val user=    RoselinDatabase.getInstance(getApplication()).userDataDao().findById(id)
                    mUser.value =user.user
                    getApplication<Roselin>().toast(twitterExceptionMessage(e))
                }
            }
        }
    }

    fun muteUser() {
        realm.executeTransaction {
            it.createObject(MuteObject::class.java)
                    .apply {
                        id = mUser.value!!.id
                        user = mUser.value?.getSerialized()
                    }
        }
        getApplication<Roselin>().toast("ミュートしました")
    }

    val realm by lazy { Realm.getDefaultInstance() }

    fun changeName(string: String) {
        realm.executeTransaction {
            it.copyToRealmOrUpdate(
                    CustomProfileObject().apply {
                        id = mUser.value!!.id
                        customname = string
                    }
            )
        }
        getApplication<Roselin>().toast("変更しました")
    }

    fun revertName(){
        realm.executeTransaction {
            it.where(CustomProfileObject::class.java).equalTo("id",mUser.value!!.id).findAll().forEach {
                it.customname=null
            }
        }
        getApplication<Roselin>().toast("戻しました")
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}