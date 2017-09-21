package xyz.donot.roselinx.view.adapter

import android.content.Context
import android.support.v7.widget.AppCompatImageButton
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmBaseAdapter
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.realm.DraftObject
import xyz.donot.roselinx.util.extraUtils.inflater

class DraftAdapter(val context: Context,
                   val realmResults: OrderedRealmCollection<DraftObject>) : RealmBaseAdapter<DraftObject>(realmResults), ListAdapter {

    override fun getView(position: Int, convertView_: View?, parent: ViewGroup): View {
        var convertView = convertView_
        val viewHolder: ViewHolder
        if (convertView == null) {
            convertView = context.inflater.inflate(R.layout.item_draft, parent, false)
            viewHolder = ViewHolder()
            viewHolder.draftText = convertView.findViewById(R.id.draft_txt)
            viewHolder.deleteDraft = convertView.findViewById(R.id.delete_draft)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        val item = realmResults[position]
        viewHolder.draftText?.text = item.text
        viewHolder.deleteDraft?.setOnClickListener {
            Realm.getDefaultInstance().use {
                it.executeTransaction {
                    item.deleteFromRealm()
                }
            }
        }
        Log.d("Realm", item.toString())
        return convertView!!
    }

    inner class ViewHolder {
        var draftText: TextView? = null
        var deleteDraft: AppCompatImageButton? = null

    }
}
