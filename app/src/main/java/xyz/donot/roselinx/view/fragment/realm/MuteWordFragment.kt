package xyz.donot.roselinx.view.fragment.realm

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.android.synthetic.main.item_mute.view.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.realm.DBMute

class MuteWordFragment :AppCompatDialogFragment(){
    val realm: Realm by lazy { Realm.getDefaultInstance() }
    val adapter by lazy {MuteWordAdater(realm.where(DBMute::class.java).isNotNull("text").findAll()) }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dividerItemDecoration = DividerItemDecoration( recycler.context,
                LinearLayoutManager(activity).orientation)
        recycler.addItemDecoration(dividerItemDecoration)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter=adapter
        refresh.isEnabled=false

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.content_base_fragment, container, false)
    inner class MuteWordAdater(orderedRealmCollection: OrderedRealmCollection<DBMute>): RealmRecyclerViewAdapter<DBMute, MuteWordAdater.ViewHolder>(orderedRealmCollection,true){
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item=getItem(position)!!
            val text:String?=item.text
            holder.apply {
                mute.text=text
                background.setOnClickListener{
                    AlertDialog.Builder(activity)
                            .setTitle("削除しますか？")
                            .setPositiveButton("OK", { _ , _ ->
                                Realm.getDefaultInstance().executeTransaction{
                                    item.deleteFromRealm()
                                }
                            })
                            .setNegativeButton("キャンセル",  {  _ ,  _ -> })
                            .show()
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
                ViewHolder(layoutInflater.inflate(R.layout.item_mute,parent,false))

        inner  class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val mute: TextView =view.mute_query
            val background: LinearLayout =view.mute_background

        }


    }
}
