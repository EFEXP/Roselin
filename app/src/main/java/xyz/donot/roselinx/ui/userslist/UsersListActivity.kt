package xyz.donot.roselinx.ui.userslist

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_user_lists.*
import kotlinx.android.synthetic.main.content_user_lists.*
import twitter4j.UserList
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.newIntent

class UsersListActivity : AppCompatActivity() {

    val viewmodel: UsersListActivityViewModel by lazy { ViewModelProviders.of(this).get(UsersListActivityViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_lists)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val userId = intent.getLongExtra("userId", 0L)
        viewmodel.isSelect=intent.getBooleanExtra("isSelect",false)
        val adapter= UsersListPagerAdapter(supportFragmentManager, userId)
        viewpager_list.adapter=adapter
        viewpager_list.offscreenPageLimit = adapter.count
        viewmodel.selectedList.observe(this, Observer {
          it?.let {
              callbackMethod(it)
          }
        })
    }

    private fun callbackMethod(userList: UserList) {
        val intent = Intent().apply {
            putExtra("listId",userList. id)
            putExtra("listName",userList.name)
            putExtra("userId", intent.getLongExtra("userId", 0L))
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {
        fun newIntent(context: Context,userId:Long,isSelect:Boolean=false):Intent{
          return context.newIntent<UsersListActivity>(xyz.donot.roselinx.util.extraUtils.Bundle {
                putLong("userId",userId)
                putBoolean("isSelect",isSelect)
            })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}
class UsersListActivityViewModel(app:Application) :AndroidViewModel(app){
     var isSelect:Boolean=false
     val selectedList:MutableLiveData<UserList> = MutableLiveData()
}