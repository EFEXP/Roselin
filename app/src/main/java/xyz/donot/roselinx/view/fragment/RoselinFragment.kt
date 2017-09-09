package xyz.donot.roselinx.view.fragment

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_roselin.view.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.Bundle
import xyz.donot.roselinx.util.extraUtils.start
import xyz.donot.roselinx.util.extraUtils.startForResult
import xyz.donot.roselinx.view.activity.*
import xyz.donot.roselinx.viewmodel.MainViewModel
import kotlin.properties.Delegates


class RoselinFragment : LifecycleFragment() {

    var viewmodel: MainViewModel by Delegates.notNull()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.content_roselin, null, false)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewmodel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
        viewmodel.user.observe(this@RoselinFragment, Observer {
                    it?.let {
                        view.apply {
                            user_detail_view.setUser(it)
                            user_detail_view.setRelation(null, true)
                            val iconIntent = Intent(activity, PictureActivity::class.java).putStringArrayListExtra("picture_urls", arrayListOf(it.originalProfileImageURLHttps))
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
        view.bt_setting.setOnClickListener { activity.start<SettingsActivity>() }
        view.bt_account.setOnClickListener {  activity.startForResult<AccountSettingActivity>(0) }

        super.onViewCreated(view, savedInstanceState)
    }
}
