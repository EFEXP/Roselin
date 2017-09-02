package xyz.donot.roselin.view.adapter

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
import xyz.donot.roselin.R
import xyz.donot.roselin.model.realm.DBDraft
import xyz.donot.roselin.util.extraUtils.inflater

class DraftAdapter(val context: Context,
                   val realmResults: OrderedRealmCollection<DBDraft>) : RealmBaseAdapter<DBDraft>(realmResults), ListAdapter {

	override fun getView(position: Int, convertView_: View?, parent: ViewGroup): View {
		var convertView = convertView_
		val viewHolder: ViewHolder
		if (convertView == null) {
			convertView = context.inflater.inflate(R.layout.item_draft, parent, false)
			viewHolder = ViewHolder()
			viewHolder.draft_text = convertView.findViewById(R.id.draft_txt)
			viewHolder.delete_draft = convertView.findViewById(R.id.delete_draft)
			convertView.tag = viewHolder
		} else {
			viewHolder = convertView.tag as ViewHolder
		}
		val item = realmResults[position]
		viewHolder.draft_text?.text = item.text
		viewHolder.delete_draft?.setOnClickListener {
			Realm.getDefaultInstance().executeTransaction {
				item.deleteFromRealm()
			}
		}
		Log.d("Realm", item.toString())
		return convertView!!
	}

	inner class ViewHolder {
		var draft_text: TextView? = null
		var delete_draft: AppCompatImageButton? = null

	}
}
