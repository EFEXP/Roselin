package xyz.donot.roselinx.view.fragment.realm

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_mute.view.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.realm.MuteObject
import xyz.donot.roselinx.view.fragment.base.ARecyclerFragment

class MuteWordFragment : ARecyclerFragment(){
    val adapter by lazy { MuteWordAdapter(Realm.getDefaultInstance().where(MuteObject::class.java).isNotNull("text").findAll()) }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler.adapter=adapter
    }

    inner class MuteWordAdapter(orderedRealmCollection: OrderedRealmCollection<MuteObject>): RealmRecyclerViewAdapter<MuteObject, MuteWordAdapter.ViewHolder>(orderedRealmCollection,true){
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item=getItem(position)!!
            val text:String?=item.text
            holder.apply {
                mute.text=text+if (item.kichitsui)"(置き換え有効)" else ""
                background.setOnClickListener{
                    val tweetItem = R.array.mute
                    AlertDialog.Builder(context).setItems(tweetItem, { _, int ->
                        val selectedItem = context.resources.getStringArray(tweetItem)[int]
                        when (selectedItem) {
                            "削除" -> {
                                Realm.getDefaultInstance().use { it.executeTransaction{
                                    item.deleteFromRealm()
                                }}
                            }
                            "置き換えミュート"->{
                                Realm.getDefaultInstance().use { it.executeTransaction{
                                    item.kichitsui = item.kichitsui != true
                                }}
                            }
                        }
                    }).show()
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
                ViewHolder(layoutInflater.inflate(R.layout.item_mute,parent,false))

        inner  class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView) , LayoutContainer {
            val mute: TextView =containerView.mute_query
            val background: LinearLayout =containerView.mute_background

        }


    }
}
