package xyz.donot.roselin.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import twitter4j.PagableResponseList
import twitter4j.User
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.Bundle
import xyz.donot.roselin.view.fragment.user.UserListFragment

class UserListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        val bundle=Bundle { putLong("userId",intent.getLongExtra("userId",0L)) }
        val fragment=
          if(intent.getBooleanExtra("isFriend",true))
            FriendUserList()
          else
            FollowerUserList()

        fragment.arguments=bundle
        supportFragmentManager.beginTransaction().apply {
            add(R.id.container,fragment)
        }.commit()
    }

}
class FriendUserList:UserListFragment() {
    override fun getUserData(userId: Long,cursor:Long): PagableResponseList<User>? = twitter.getFriendsList(userId,cursor)
}
class FollowerUserList:UserListFragment() {
    override fun getUserData(userId: Long,cursor:Long): PagableResponseList<User>? =twitter.getFollowersList(userId,cursor)
}