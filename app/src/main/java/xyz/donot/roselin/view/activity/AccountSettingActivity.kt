package xyz.donot.roselin.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.content_account_setting.*
import twitter4j.User
import xyz.donot.roselin.R
import xyz.donot.roselin.model.realm.DBAccount
import xyz.donot.roselin.util.extraUtils.start
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.util.getDeserialized
import xyz.donot.roselin.view.adapter.UserListAdapter



class AccountSettingActivity : AppCompatActivity() {
    val adapter by lazy { UserListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.title="アカウントの選択"
        val dividerItemDecoration = DividerItemDecoration( recycler_account.context,
                LinearLayoutManager(this).orientation)
        recycler_account.addItemDecoration(dividerItemDecoration)
        recycler_account.layoutManager = LinearLayoutManager(this)
        recycler_account.adapter=adapter
        adapter.setOnItemLongClickListener { _, _, position ->
            Realm.getDefaultInstance().use {
                it.executeTransaction {
                    it.where(DBAccount::class.java).equalTo("id",adapter.data[position].id).findFirst().deleteFromRealm()

                }
                toast("削除しました")
                adapter.notifyItemRemoved(position)
             true
            }
        }
        adapter.setOnItemClickListener { _, _, position ->
            Realm.getDefaultInstance().use {
                it.executeTransaction {
                    it.where(DBAccount::class.java).equalTo("isMain", true).findFirst().isMain = false
                    it.where(DBAccount::class.java).equalTo("id",adapter.data[position].id).findFirst().apply {
                        isMain = true
                    }
                }
                val data = Intent()
                data.putExtra("accountChanged", true)
                setResult(AppCompatActivity.RESULT_OK, data)
                finish()
        }
    }
        val result=  Realm.getDefaultInstance().where(DBAccount::class.java).findAll()
        val users=result.map { it.user }.map { it?.getDeserialized<User>() }
        adapter.addData(users)
        fab.setOnClickListener { _->
            start<OauthActivity>()
        }
}
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}

