package xyz.donot.roselinx.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.ImageView
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.listener.OnItemDragListener
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_tab_setting.*
import twitter4j.Query
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.realm.*
import xyz.donot.roselinx.util.extraUtils.newIntent
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.util.getMyScreenName
import xyz.donot.roselinx.util.getSerialized
import xyz.donot.roselinx.view.custom.MyItemDragAndSwipeCallback
import xyz.donot.roselinx.view.fragment.SearchSettingFragment


class TabSettingActivity : AppCompatActivity() {
    private val REQUEST_LISTS = 1

    private val mAdapter by lazy { TabItemAdapter(arrayListOf()) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_setting)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        //Listener
        val onItemDragListener = object : OnItemDragListener {
            override fun onItemDragMoving(source: RecyclerView.ViewHolder?, from: Int, target: RecyclerView.ViewHolder?, to: Int) = Unit
            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) = Unit
            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) = realmRecreate()
        }
        val dividerItemDecoration = DividerItemDecoration(tab_recycler.context, LinearLayoutManager(this@TabSettingActivity).orientation)
        tab_recycler.addItemDecoration(dividerItemDecoration)
        tab_recycler.layoutManager = LinearLayoutManager(this)
        val data = Realm.getDefaultInstance().where(DBTabData::class.java).findAll()
        data.forEach {
            mAdapter.addData(Realm.getDefaultInstance().copyFromRealm(it))
        }
        val itemDragAndSwipeCallback = MyItemDragAndSwipeCallback(mAdapter)
        val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
        itemTouchHelper.attachToRecyclerView(tab_recycler)
        //Configure Adapter
        mAdapter.enableSwipeItem()
        mAdapter.enableDragItem(itemTouchHelper, R.id.iv_draggable, false)
        mAdapter.setOnItemDragListener(onItemDragListener)
        mAdapter.setOnItemClickListener { _, _, position ->
            if (mAdapter.getItem(position)?.type!= SETTING)
            AlertDialog.Builder(this@TabSettingActivity)
                    .setTitle("削除しますか？")
                    .setPositiveButton("OK", { _, _ ->
                        mAdapter.remove(position)
                    })
                    .setNegativeButton("キャンセル", { _, _ -> })
                    .show()
        }
        tab_recycler.adapter = mAdapter
        fab.setOnClickListener {
            val tab_menu = R.array.add_tab_menu
            AlertDialog.Builder(this@TabSettingActivity)
                    .setItems(tab_menu, { _, int ->
                        val selectedItem = resources.getStringArray(tab_menu)[int]
                        when (selectedItem) {
                            "ホーム" -> {
                                mAdapter.addData(DBTabData().apply {
                                    type = HOME
                                    accountId = getMyId()
                                    screenName = getMyScreenName()
                                })
                                realmRecreate()
                            }
                            "リスト" -> {
                                startActivityForResult(newIntent<UserListsActivity>().apply {
                                    putExtra("userId", getMyId())
                                    putExtra("selectList", true)
                                }, REQUEST_LISTS)
                            }
                            "リプライ" -> {
                                mAdapter.addData(DBTabData().apply {
                                    type = MENTION
                                    accountId = getMyId()
                                    screenName = getMyScreenName()
                                })
                                realmRecreate()
                            }
                            "トレンド" -> {
                                mAdapter.addData(DBTabData().apply {
                                    type = TREND
                                })
                                realmRecreate()
                            }
                            "ダイレクトメール" -> {
                                mAdapter.addData(DBTabData().apply {
                                    type = DM
                                    accountId = getMyId()
                                    screenName = getMyScreenName()
                                })
                                realmRecreate()
                            }
                            "検索" -> {
                                SearchSettingFragment().show(supportFragmentManager, "")
                            }

                            "通知" -> {
                                mAdapter.addData(DBTabData().apply {
                                    type = NOTIFICATION
                                    accountId = getMyId()
                                    screenName = getMyScreenName()
                                })
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
        mAdapter.addData(DBTabData().apply {
            type = SEARCH
            searchQuery = query.getSerialized()
            searchWord = querytext
        })

        realmRecreate()
    }

    fun realmRecreate()
            = Realm.getDefaultInstance().use {
        it.executeTransaction { realm ->
            realm.where(DBTabData::class.java).findAll().deleteAllFromRealm()
            for (i in 0 until mAdapter.data.size) {
                val data = mAdapter.data[i]
                realm.createObject(DBTabData::class.java).apply {
                    type = data.type
                    order = i
                    screenName = data.screenName
                    listId = data.listId
                    listName = data.listName
                    accountId = data.accountId
                    searchQuery = data.searchQuery
                    searchWord = data.searchWord
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LISTS) {
                mAdapter.addData(DBTabData().apply {
                    type = LIST
                    listName = data.getStringExtra("listName")
                    listId = data.getLongExtra("listId", 0L)
                    accountId = data.getLongExtra("userId", 0L)

                })
            }
            realmRecreate()
        }

    }

    inner class TabItemAdapter(list: ArrayList<DBTabData>) : BaseItemDraggableAdapter<DBTabData, BaseViewHolder>(R.layout.item_tabs_setting, list) {
        override fun convert(helper: BaseViewHolder, item: DBTabData) {
            helper.apply {
                val text = ConvertToName(item.type)
                setText(R.id.tv_screenname, item.screenName.toString())
                when (item.type) {
                    HOME -> {
                        setText(R.id.tv_tabname, text)
                    }
                    MENTION -> {
                        setText(R.id.tv_tabname, text)
                    }
                    LIST -> {
                        setText(R.id.tv_tabname, item.listName)
                    }
                    NOTIFICATION -> {
                        setText(R.id.tv_tabname, text)
                    }
                    TREND -> {
                        setText(R.id.tv_tabname, text)
                    }
                    SEARCH -> {
                        setText(R.id.tv_tabname, item.searchWord)
                    }
                    DM -> {
                        setText(R.id.tv_tabname, text)
                    }
                    SETTING -> {
                        setText(R.id.tv_tabname, text)
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
                getView<ImageView>(R.id.iv_icon).setImageDrawable(image)
            }

        }
    }
}
