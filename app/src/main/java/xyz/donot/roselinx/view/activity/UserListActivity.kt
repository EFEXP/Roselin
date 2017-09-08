package xyz.donot.roselinx.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import twitter4j.PagableResponseList
import twitter4j.User
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.Bundle
import xyz.donot.roselinx.util.extraUtils.intent
import xyz.donot.roselinx.view.fragment.user.UserListFragment

class UserListActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_user_list)
		val bundle = Bundle { putLong("userId", intent.getLongExtra("userId", 0L)) }
		val fragment =
				if (intent.getBooleanExtra("isFriend", true))
					FriendUserList()
				else
					FollowerUserList()
		fragment.arguments = bundle
		supportFragmentManager.beginTransaction().apply {
			add(R.id.container, fragment)
		}.commit()
	}

}

class FriendUserList : UserListFragment() {
	override fun getUserData(userId: Long, cursor: Long): PagableResponseList<User>? = viewmodel.twitter.getFriendsList(userId, cursor)
	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
        viewmodel .adapter.setOnItemClickListener { _, _, position ->
			val intent = activity.intent<UserActivity>()
			intent.putExtra("user_id", viewmodel .adapter.data[position].id)
			activity.startActivity(intent)
		}
	}
}

class FollowerUserList : UserListFragment() {
	override fun getUserData(userId: Long, cursor: Long): PagableResponseList<User>? = viewmodel.twitter.getFollowersList(userId, cursor)
	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
        viewmodel .adapter.setOnItemClickListener { _, _, position ->
			val intent = activity.intent<UserActivity>()
			intent.putExtra("user_id", viewmodel .adapter.data[position].id)
			activity.startActivity(intent)
		}
	}
}
