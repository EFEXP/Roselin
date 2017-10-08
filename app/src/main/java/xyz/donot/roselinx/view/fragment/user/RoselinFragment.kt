package xyz.donot.roselinx.view.fragment.user

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_roselin.*
import kotlinx.android.synthetic.main.content_roselin.view.*
import twitter4j.Query
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.*
import xyz.donot.roselinx.util.getAccount
import xyz.donot.roselinx.util.getDragdismiss
import xyz.donot.roselinx.view.activity.*
import xyz.donot.roselinx.viewmodel.activity.MainViewModel


class RoselinFragment : Fragment() {
    val viewmodel: MainViewModel by lazy { ViewModelProviders.of(activity).get(MainViewModel::class.java) }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.content_roselin, null, false)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.user.observe(this@RoselinFragment, Observer {
            it?.let {
                val iconIntent = activity.getDragdismiss(PictureActivity.createIntent(activity,arrayListOf(it.originalProfileImageURLHttps)))
                view.user_detail_view.apply {
                    iconClick = { startActivity(iconIntent) }
                    listClick = { activity.start<UsersListActivity>(Bundle { putLong("userId", it.id) }) }
                    friendClick = {
                        activity.start<UserListActivity>(Bundle {
                            putLong("userId", it.id)
                            putBoolean("isFriend", true)
                        })
                    }
                    followerClick = {
                        activity.start<UserListActivity>(Bundle {
                            putLong("userId", it.id)
                            putBoolean("isFriend", false)
                        })
                    }
                    editClick = { activity.start<EditProfileActivity>() }
                    user_detail_view.setUser(it)
                    user_detail_view.setRelation(null, true)
                    user_detail_view.iconClick = { startActivity(iconIntent) }
                    user_detail_view.listClick = { activity.start<UsersListActivity>(Bundle { putLong("userId", it.id) }) }
                    user_detail_view.friendClick = {
                        activity.start<UserListActivity>(Bundle {
                            putLong("userId", it.id)
                            putBoolean("isFriend", true)
                        })
                    }
                    user_detail_view.followerClick = {
                        activity.start<UserListActivity>(Bundle {
                            putLong("userId", it.id)
                            putBoolean("isFriend", false)
                        })
                    }
                    user_detail_view.editClick = { activity.start<EditProfileActivity>() }
                }
            }
        })
        viewmodel.isConnectedStream.observe(this@RoselinFragment, Observer {
            it?.let {
                mainThread {
                    if (this.isAdded)
                        view.iv_connected_stream.setImageDrawable(ResourcesCompat.getDrawable(resources, if (it) R.drawable.ic_cloud else R.drawable.ic_cloud_off, null))
                }
            }
        })
        view.bt_profile.onClick = {
            activity.startActivity(context.getDragdismiss(context.newIntent<UserActivity>(Bundle().apply { putLong("user_id", getAccount().id) })))
        }
        view.bt_setting.onClick = { activity.start<SettingsActivity>() }
        view.bt_account.onClick = { activity.startForResult<AccountSettingActivity>(0) }
        view.bt_search.onClick = { activity.start<SearchSettingActivity>() }
        search_view.setOnQueryTextListener(
            object :SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    if (!p0.isNullOrEmpty()) {
                        val q = Query()
                        var string = p0
                        string += " exclude:nativeretweets"
                        q.apply {
                            query = string
                            resultType = Query.MIXED
                        }
                        startActivity(SearchActivity.createIntent(activity, q, p0))
                        search_view.setQuery("",false)
                        search_view.onActionViewCollapsed()
                        search_view.clearFocus()
                    }
                    return false
                }


                override fun onQueryTextChange(p0: String?): Boolean {

                    return false

                }

            })



}}
