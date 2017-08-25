package xyz.donot.roselin.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.ImageView
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.squareup.picasso.Picasso
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_tab_setting.*
import xyz.donot.roselin.R
import xyz.donot.roselin.model.realm.DBTabData


class TabSettingActivity : AppCompatActivity() {

  private  val mAdapter by lazy { TabItemAdapter(arrayListOf()) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_setting)
       val onItemDragListener = object : OnItemDragListener {
           override fun onItemDragMoving(source: RecyclerView.ViewHolder?, from: Int, target: RecyclerView.ViewHolder?, to: Int) {
           }

           override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
           }

           override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
           }
       }
        val dividerItemDecoration = DividerItemDecoration( tab_recycler.context, LinearLayoutManager(this@TabSettingActivity).orientation)
        tab_recycler.addItemDecoration(dividerItemDecoration)
        tab_recycler.layoutManager = LinearLayoutManager(this)
        Realm.getDefaultInstance().where(DBTabData::class.java).findAll().forEach { mAdapter.addData(it) }
        val itemDragAndSwipeCallback = ItemDragAndSwipeCallback(mAdapter)
        val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
        itemTouchHelper.attachToRecyclerView(tab_recycler)
        mAdapter.enableDragItem(itemTouchHelper, R.id.iv_draggable, false)
        mAdapter.setOnItemDragListener(onItemDragListener)
        tab_recycler.adapter=mAdapter

    }

    inner class TabItemAdapter(list: ArrayList<DBTabData>): BaseItemDraggableAdapter<DBTabData, BaseViewHolder>(R.layout.item_tabs_setting,list){
        override fun convert(helper: BaseViewHolder,item: DBTabData) {
            helper.apply {
                setText(R.id.tv_tabname,item.name)
                setText(R.id.tv_screenname,item.accountId.toString())
             val t=   when (item.name){
                    "Home"->{R.drawable.ic_home}
                    "Mention"->{R.drawable.ic_reply}
                 else->{throw IllegalStateException()}
                }
                Picasso.with(this@TabSettingActivity).load(t).into(getView<ImageView>(R.id.iv_icon))

            }

        }
    }
}
