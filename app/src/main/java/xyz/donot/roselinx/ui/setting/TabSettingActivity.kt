package xyz.donot.roselinx.ui.setting

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import kotlinx.android.synthetic.main.activity_tab_setting.*
import kotlinx.android.synthetic.main.item_tabs_setting.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Query
import xyz.donot.roselinx.R
import xyz.donot.roselinx.customrecycler.DraggableAdapter
import xyz.donot.roselinx.customrecycler.ItemDragAndSwipeCallback
import xyz.donot.roselinx.model.entity.*
import xyz.donot.roselinx.ui.search.SearchSettingFragment
import xyz.donot.roselinx.ui.status.KViewHolder
import xyz.donot.roselinx.ui.userslist.UsersListActivity
import xyz.donot.roselinx.ui.util.getAccount


class TabSettingActivity : AppCompatActivity() {
   companion object {
       private const val REQUEST_LISTS = 1
   }
    val account by lazy { getAccount() }

    private val mAdapter by lazy { TabItemAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_setting)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        //Listener

        val dividerItemDecoration = DividerItemDecoration(tab_recycler.context, LinearLayoutManager(this@TabSettingActivity).orientation)
        tab_recycler.addItemDecoration(dividerItemDecoration)
        tab_recycler.layoutManager = LinearLayoutManager(this)
        ItemTouchHelper(ItemDragAndSwipeCallback(mAdapter)).attachToRecyclerView(tab_recycler)
        mAdapter.onMoveEnd={realmRecreate()}


        launch(UI) {
            async {
                RoselinDatabase.getInstance().savedTabDao().getAllLiveData().observe(this@TabSettingActivity, Observer {
                    it?.let {
                        mAdapter.itemList = it
                    }
                })
            }
        }


        //Configure Adapter
        mAdapter.onItemClick = { item, _ ->
            if (item.type != SETTING)
                AlertDialog.Builder(this@TabSettingActivity)
                        .setTitle(getString(R.string.dialog_question_delete))
                        .setPositiveButton(getString(R.string.dialog_OK), { _, _ ->
                            launch(UI) {
                                async { RoselinDatabase.getInstance().savedTabDao().delete(item) }.await()
                                realmRecreate()
                            }

                        })
                        .setNegativeButton(getString(R.string.dialog_cancel), { _, _ -> })
                        .show()
        }
        tab_recycler.adapter = mAdapter
        fab.setOnClickListener {
            val tabMenu = R.array.add_tab_menu
            AlertDialog.Builder(this@TabSettingActivity)
                    .setItems(tabMenu, { _, int ->
                        val selectedItem = resources.getStringArray(tabMenu)[int]
                        when (selectedItem) {
                            getString(R.string.tabname_home) -> {
                                SavedTab.save(SavedTab(type = HOME, accountId = account.id, screenName =account.user.screenName))
                                realmRecreate()
                            }
                            getString(R.string.tabname_lost) -> {
                                startActivityForResult(UsersListActivity.newIntent(this, account.id, true), REQUEST_LISTS)
                            }
                            getString(R.string.tabname_reply) -> {
                                SavedTab.save(SavedTab(type = MENTION,
                                        accountId = account.id,
                                        screenName =account.user.screenName))
                                realmRecreate()
                            }
                            getString(R.string.tabname_trend) -> {
                                SavedTab.save( SavedTab(type = TREND))
                                realmRecreate()
                            }
                            getString(R.string.tabname_dm) -> {
                                SavedTab.save( SavedTab(type = DM,
                                        accountId = account.id,
                                        screenName = account.user.screenName))
                                realmRecreate()
                            }
                            getString(R.string.tabname_search) -> {
                                SearchSettingFragment().show(supportFragmentManager, "")
                            }

                            getString(R.string.tabname_notification) -> {
                                SavedTab.save(SavedTab(type = NOTIFICATION,
                                        accountId = account.id,
                                        screenName =account.user.screenName))
                                realmRecreate()
                            }
                        }
                    }).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    fun setSearchWord(query: Query, querytext: String) {
        SavedTab.save( SavedTab(type = SEARCH,
                searchQuery = query,
                searchWord = querytext))

        realmRecreate()
    }

    private fun realmRecreate() {
        launch {
            RoselinDatabase.getInstance().savedTabDao().deleteAll()
            (0 until mAdapter.itemList.size)
                    .map { mAdapter.itemList[it] }
                    .forEach {
                        SavedTab.save( SavedTab(
                                type = it.type,
                                screenName = it.screenName,
                                listId = it.listId,
                                listName = it.listName,
                                accountId = it.accountId,
                                searchQuery = it.searchQuery,
                                searchWord = it.searchWord
                        ))
                    }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LISTS) {
                SavedTab.save( SavedTab(
                        type = LIST,
                        listName = data.getStringExtra("listName"),
                        listId = data.getLongExtra("listId", 0L),
                        accountId = data.getLongExtra("userId", 0L)
                ))
            }
            realmRecreate()
        }

    }

    inner class TabItemAdapter : DraggableAdapter<SavedTab>(R.layout.item_tabs_setting) {
        override fun onBindViewHolder(holder: KViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val item = mAdapter.itemList[position]
            holder.containerView.apply {
                val text = toName(item.type)
                tv_screenname.text = item.screenName.toString()
                when (item.type) {
                    HOME -> {
                        tv_tabname.text = text
                    }
                    MENTION -> {
                        tv_tabname.text = text
                    }
                    LIST -> {
                        tv_tabname.text = item.listName
                    }
                    NOTIFICATION -> {
                        tv_tabname.text = text
                    }
                    TREND -> {
                        tv_tabname.text = text
                    }
                    SEARCH -> {
                        tv_tabname.text = item.searchWord
                    }
                    DM -> {
                        tv_tabname.text = text
                    }
                    SETTING -> {
                        tv_tabname.text = text
                    }
                    else -> {
                        throw IllegalArgumentException()
                    }
                }
                val image = ResourcesCompat.getDrawable(resources, when (item.type) {
                    HOME -> {
                        R.drawable.ic_home
                    }
                    MENTION -> {
                        R.drawable.ic_reply
                    }
                    LIST -> {
                        R.drawable.ic_view_list
                    }
                    NOTIFICATION -> {
                        R.drawable.ic_notifications
                    }
                    TREND -> {
                        R.drawable.ic_trending
                    }
                    SEARCH -> {
                        R.drawable.ic_search
                    }
                    DM -> {
                        R.drawable.ic_mail
                    }
                    SETTING -> {
                        R.drawable.ic_settings_grey_400_36dp
                    }
                    else -> {
                        throw IllegalArgumentException()
                    }
                }, null)
                iv_icon.setImageDrawable(image)
            }

        }
    }
}
