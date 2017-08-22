package xyz.donot.roselin.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.Bundle
import xyz.donot.roselin.view.fragment.UserListFragment

class UserListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        val bundle=Bundle { putLong("userId",intent.getLongExtra("userId",0L)) }
        val fragment=UserListFragment().apply { arguments=bundle}
        supportFragmentManager.beginTransaction().apply {
            add(R.id.container,fragment)
        }.commit()

    }
}
