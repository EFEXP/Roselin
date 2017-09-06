package xyz.donot.roselinx.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_user_lists.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.Bundle
import xyz.donot.roselinx.view.fragment.status.UsersListFragment

class UserListsActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_user_lists)
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setDisplayShowHomeEnabled(true)
		val userId = intent.getLongExtra("userId", 0L)
		val selectList = intent.getBooleanExtra("selectList", false)
		val fragment = UsersListFragment().apply {
			arguments = Bundle {
				putLong("userId", userId)
				putBoolean("selectList", selectList)
			}


		}
		supportFragmentManager.beginTransaction().add(R.id.container_user_lists, fragment).commit()
	}

	fun callbackMethod(listId: Long, listName: String) {
		val intent = Intent().apply {
			putExtra("listId", listId)
			putExtra("listName", listName)
			putExtra("userId", intent.getLongExtra("userId", 0L))
		}
		setResult(Activity.RESULT_OK, intent)
		finish()
	}

	override fun onSupportNavigateUp(): Boolean {
		onBackPressed()
		return super.onSupportNavigateUp()
	}

}