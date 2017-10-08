package xyz.donot.roselinx.view.activity

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.content_account_setting.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.room.RoselinDatabase
import xyz.donot.roselinx.model.room.UserData
import xyz.donot.roselinx.util.extraUtils.start
import xyz.donot.roselinx.util.extraUtils.toast
import xyz.donot.roselinx.view.adapter.TwitterUserAdapter


class AccountSettingActivity : AppCompatActivity() {
    val adapter by lazy { TwitterUserAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.title = "アカウントの選択"
        val dividerItemDecoration = DividerItemDecoration(recycler_account.context,
                LinearLayoutManager(this).orientation)
        recycler_account.addItemDecoration(dividerItemDecoration)
        recycler_account.layoutManager = LinearLayoutManager(this)
        recycler_account.adapter = adapter
        adapter.onItemLongClick= {  _, position ->
            launch {
                RoselinDatabase.getInstance().twitterAccountDao().deleteById(adapter.itemList[position].id)
            }
                toast("削除しました")
                adapter.notifyItemRemoved(position)
        }
        adapter.onItemClick={_,position->
            launch (UI){
                async {
                    val dao=RoselinDatabase.getInstance().twitterAccountDao()
                    val selected= dao.findById(adapter.itemList[position].id).copy(isMain = true)
                    dao.update(selected)
                    val wasMain=dao.getMainAccount(true).copy(isMain = false)
                    dao.update(wasMain)
                }.await()
                val data = Intent()
                data.putExtra("accountChanged", true)
                setResult(AppCompatActivity.RESULT_OK, data)
                finish()
            }
        }
        launch (UI){
            async { RoselinDatabase.getInstance().twitterAccountDao().allData() }.await()
                    .observe(this@AccountSettingActivity, Observer {
                        it?.let {
                            adapter.itemList=it.map { it.user }.map { UserData(it,it.screenName,it.id) }
                        }
                    })
        }


        fab.setOnClickListener { _ ->
            start<OauthActivity>()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}

