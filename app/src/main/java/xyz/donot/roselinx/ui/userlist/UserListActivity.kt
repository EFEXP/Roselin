package xyz.donot.roselinx.ui.userlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import twitter4j.PagableResponseList
import twitter4j.User
import xyz.donot.roselinx.R
import xyz.donot.roselinx.ui.detailuser.UserActivity
import xyz.donot.roselinx.ui.util.extraUtils.bundle
import xyz.donot.roselinx.ui.util.extraUtils.intent
import xyz.donot.roselinx.ui.util.extraUtils.newIntent

class UserListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        val bundle = bundle { putLong("userId", intent.getLongExtra("userId", 0L)) }
        val fragment = if (intent.getBooleanExtra("isFriend", true))
            FriendUserList()
        else
            FollowerUserList()
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().apply {
            add(R.id.container, fragment)
        }.commit()
    }

    companion object {
        fun newIntent(context: Context,isFriend:Boolean,userId: Long): Intent {
            return context.newIntent<UserListActivity>(bundle {
                putBoolean("isFriend", isFriend)
                putLong("userId", userId)

            })
        }
    }
}

class FriendUserList : UserListFragment() {

    override fun getUserData(userId: Long, cursor: Long): PagableResponseList<User>? = viewmodel.twitter.getFriendsList(userId, cursor)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.adapter!!.setOnItemClickListener { _, _, position ->
            val intent = activity.intent<UserActivity>()
            intent.putExtra("user_id", viewmodel.adapter!!.data[position].id)
            activity.startActivity(intent)
        }
    }
}

class FollowerUserList : UserListFragment() {
    override fun getUserData(userId: Long, cursor: Long): PagableResponseList<User>? = viewmodel.twitter.getFollowersList(userId, cursor)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.adapter!!.setOnItemClickListener { _, _, position ->
            val intent = activity.intent<UserActivity>()
            intent.putExtra("user_id", viewmodel.adapter!!.data[position].id)
            activity.startActivity(intent)
        }
    }
}
