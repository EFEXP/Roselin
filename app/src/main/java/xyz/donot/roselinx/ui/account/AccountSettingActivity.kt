package xyz.donot.roselinx.ui.account

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
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
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.ui.oauth.OauthActivity
import xyz.donot.roselinx.util.extraUtils.start
import xyz.donot.roselinx.util.extraUtils.toast
import xyz.donot.roselinx.ui.userlist.TwitterAccountAdapter


class AccountSettingActivity : AppCompatActivity() {
    val adapter by lazy { TwitterAccountAdapter() }
    val viewmodel: AccountSettingViewModel by lazy { ViewModelProviders.of(this).get(AccountSettingViewModel::class.java) }
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
        adapter.onItemLongClick = { item, position ->
            launch {
                RoselinDatabase.getInstance().twitterAccountDao().deleteById(item.id)
            }
            toast("削除しました")
            adapter.notifyItemRemoved(position)
        }
        adapter.onItemClick = { item, _ ->
            launch(UI) {
                async {
                    val dao = RoselinDatabase.getInstance().twitterAccountDao()
                    val selected = dao.findById(item.id).copy(isMain = true)
                    dao.update(selected)
                    val wasMain = dao.getMainAccount(true).copy(isMain = false)
                    dao.update(wasMain)
                }.await()
                val data = Intent()
                data.putExtra("accountChanged", true)
                setResult(AppCompatActivity.RESULT_OK, data)
                finish()
            }
        }


        viewmodel.dataSource.observe(this@AccountSettingActivity, Observer {
            it?.let {
                adapter.setList(it)
            }
        })

        fab.setOnClickListener { _ ->
            start<OauthActivity>()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}

class AccountSettingViewModel : ViewModel() {
    val dataSource = RoselinDatabase.getInstance().twitterAccountDao().allData()
            .create(0, PagedList.Config.Builder().setPageSize(50).setPrefetchDistance(50).build())
}