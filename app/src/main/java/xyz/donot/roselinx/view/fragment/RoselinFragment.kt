package xyz.donot.roselinx.view.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_roselin.view.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.Bundle
import xyz.donot.roselinx.util.extraUtils.mainThread
import xyz.donot.roselinx.util.extraUtils.start
import xyz.donot.roselinx.util.extraUtils.startForResult
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.view.activity.*
import xyz.donot.roselinx.viewmodel.activity.MainViewModel
import kotlin.properties.Delegates


class RoselinFragment : Fragment() {

    var viewmodel: MainViewModel by Delegates.notNull()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.content_roselin, null, false)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewmodel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
        viewmodel.user.observe(this@RoselinFragment, Observer {
                    it?.let {
                        val iconIntent = Intent(activity, PictureActivity::class.java).putStringArrayListExtra("picture_urls", arrayListOf(it.originalProfileImageURLHttps))
                        view.user_detail_view.apply {
                            iconClick = { startActivity(iconIntent) }
                            listClick = { activity.start<UserListsActivity>(Bundle { putLong("userId", it.id) }) }
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
                            editClick={ activity.start<EditProfileActivity>() }
                            user_detail_view.setUser(it)
                            user_detail_view.setRelation(null, true)
                            user_detail_view.iconClick = { startActivity(iconIntent) }
                            user_detail_view.listClick = { activity.start<UserListsActivity>(Bundle { putLong("userId", it.id) }) }
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
                    if (activity!=null)
                        view.iv_connected_stream.setImageDrawable(ResourcesCompat.getDrawable(resources, if (it) R.drawable.ic_cloud else R.drawable.ic_cloud_off, null))
                }
            }
        })
        view.bt_profile.onClick= { activity. start<UserActivity>(Bundle().apply { putLong("user_id", getMyId()) })}
        view.bt_setting.onClick= { activity.start<SettingsActivity>() }
        view.bt_account.onClick={  activity.startForResult<AccountSettingActivity>(0) }
        view.bt_search.onClick={  activity.start<SearchSettingActivity>() }
        super.onViewCreated(view, savedInstanceState)
    }
}
