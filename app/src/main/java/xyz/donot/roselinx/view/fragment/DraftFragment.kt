package xyz.donot.roselinx.view.fragment

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import io.realm.Realm
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.realm.DBDraft
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.view.activity.EditTweetActivity
import xyz.donot.roselinx.view.adapter.DraftAdapter


class DraftFragment : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.fragment_draft, container, false)
        val realm= Realm.getDefaultInstance()
                .where(DBDraft::class.java)
                .equalTo("accountId",getMyId())
                .findAll()
        val mAdapter= DraftAdapter(context = context,realmResults =realm)
        val list: ListView =view.findViewById(R.id.draft_list_view)
        list.adapter=mAdapter
        list.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _->
            val parent_list=parent as ListView
            val item=parent_list.getItemAtPosition(position)as DBDraft
            if(activity is EditTweetActivity){
                (activity as EditTweetActivity) .changeDraft(item)
                this@DraftFragment.dismiss()
            }
            Realm.getDefaultInstance().executeTransaction { item.deleteFromRealm() }
        }
        return view
    }

}
