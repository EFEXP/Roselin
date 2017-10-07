package xyz.donot.roselinx.view.activity

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_tab_setting.*
import kotlinx.android.synthetic.main.item_tabs_setting.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Query
import xyz.donot.roselinx.R
import xyz.donot.roselinx.customrecycler.DraggableAdapter
import xyz.donot.roselinx.customrecycler.ItemDragAndSwipeCallback
import xyz.donot.roselinx.model.room.*
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.util.getMyScreenName
import xyz.donot.roselinx.view.fragment.SearchSettingFragment


class TabSettingActivity : AppCompatActivity() {
    private val REQUEST_LISTS = 1

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
                RoselinDatabase.getInstance(this@TabSettingActivity).savedTabDao().getAllLiveData().observe(this@TabSettingActivity, Observer {
                    it?.let {
                        mAdapter.itemList = it
                    }
                })
            }
        }


        //Configure Adapter
        mAdapter.onItemClick = { item, position ->
            if (item.type != SETTING)
                AlertDialog.Builder(this@TabSettingActivity)
                        .setTitle("削除しますか？")
                        .setPositiveButton("OK", { _, _ ->
                            launch(UI) {
                                async { RoselinDatabase.getInstance(this@TabSettingActivity).savedTabDao().delete(item) }.await()
                                realmRecreate()
                            }

                        })
                        .setNegativeButton("キャンセル", { _, _ -> })
                        .show()
        }
        tab_recycler.adapter = mAdapter
        fab.setOnClickListener {
            val tabMenu = R.array.add_tab_menu
            AlertDialog.Builder(this@TabSettingActivity)
                    .setItems(tabMenu, { _, int ->
                        val selectedItem = resources.getStringArray(tabMenu)[int]
                        when (selectedItem) {
                            "ホーム" -> {
                                SavedTab.save(this@TabSettingActivity, SavedTab(type = HOME, accountId = getMyId(), screenName = getMyScreenName()))
                                realmRecreate()
                            }
                            "リスト" -> {
                                startActivityForResult(UsersListActivity.newIntent(this, getMyId(), true), REQUEST_LISTS)
                            }
                            "リプライ" -> {
                                SavedTab.save(this@TabSettingActivity, SavedTab(type = MENTION,
                                        accountId = getMyId(),
                                        screenName = getMyScreenName()))
                                realmRecreate()
                            }
                            "トレンド" -> {
                                SavedTab.save(this@TabSettingActivity, SavedTab(type = TREND))
                                realmRecreate()
                            }
                            "ダイレクトメール" -> {
                                SavedTab.save(this@TabSettingActivity, SavedTab(type = DM,
                                        accountId = getMyId(),
                                        screenName = getMyScreenName()))
                                realmRecreate()
                            }
                            "検索" -> {
                                SearchSettingFragment().show(supportFragmentManager, "")
                            }

                            "通知" -> {
                                SavedTab.save(this@TabSettingActivity, SavedTab(type = NOTIFICATION,
                                        accountId = getMyId(),
                                        screenName = getMyScreenName()))
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
        SavedTab.save(this@TabSettingActivity, SavedTab(type = SEARCH,
                searchQuery = query,
                searchWord = querytext))

        realmRecreate()
    }

    fun realmRecreate() {
        launch {
            RoselinDatabase.getInstance(this@TabSettingActivity).savedTabDao().deleteAll()
            for (i in 0 until mAdapter.itemList.size) {
                val data = mAdapter.itemList[i]
                SavedTab.save(this@TabSettingActivity, SavedTab(
                        type = data.type,
                        screenName = data.screenName,
                        listId = data.listId,
                        listName = data.listName,
                        accountId = data.accountId,
                        searchQuery = data.searchQuery,
                        searchWord = data.searchWord
                ))
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LISTS) {
                SavedTab.save(this@TabSettingActivity, SavedTab(
                        type = LIST,
                        listName = data.getStringExtra("listName"),
                        listId = data.getLongExtra("listId", 0L),
                        accountId = data.getLongExtra("userId", 0L)
                ))
            }
            realmRecreate()
        }

    }

    inner class TabItemAdapter : DraggableAdapter<TabItemAdapter.ViewHolder, SavedTab>() {

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val item = mAdapter.itemList[position]
            holder.apply {
                val text = toName(item.type)
                screenname.text = item.screenName.toString()
                when (item.type) {
                    HOME -> {
                        name.text = text

                    }
                    MENTION -> {
                        name.text = text
                    }
                    LIST -> {
                        name.text = item.listName
                    }
                    NOTIFICATION -> {
                        name.text = text
                    }
                    TREND -> {
                        name.text = text
                    }
                    SEARCH -> {
                        name.text = item.searchWord
                    }
                    DM -> {
                        name.text = text
                    }
                    SETTING -> {
                        name.text = text
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
                icon.setImageDrawable(image)
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
                ViewHolder(layoutInflater.inflate(R.layout.item_tabs_setting, parent, false))

        inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
            val name: TextView = containerView.tv_tabname
            val screenname: TextView = containerView.tv_screenname
            val icon: ImageView = containerView.iv_icon

        }
    }
}
