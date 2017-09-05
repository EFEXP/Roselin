package xyz.donot.roselin.view.fragment.realm

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.text.emoji.widget.EmojiAppCompatTextView
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.android.synthetic.main.item_user.view.*
import twitter4j.User
import xyz.donot.roselin.R
import xyz.donot.roselin.model.realm.DBMute
import xyz.donot.roselin.util.getDeserialized


class MuteUserFragment : AppCompatDialogFragment(){
    val realm: Realm by lazy { Realm.getDefaultInstance() }
    val adapter by lazy {MuteUserAdater(realm.where(DBMute::class.java).notEqualTo("id",0L).findAll()) }


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
    inner class MuteUserAdater(orderedRealmCollection: OrderedRealmCollection<DBMute>): RealmRecyclerViewAdapter<DBMute, MuteUserAdater.ViewHolder>(orderedRealmCollection,true){

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item=getItem(position)!!
            val user=item.user?.getDeserialized<User>()
            holder.apply {
                screenname.text="@"+user?.screenName
                Picasso.with(activity).load(user?.biggerProfileImageURLHttps).into(icon)
                username.text=user?.name
                description.text=user?.description
                background.setOnClickListener {
                    AlertDialog.Builder(activity)
                            .setTitle("削除しますか？")
                            .setPositiveButton("OK", {_, _ ->
                                Realm.getDefaultInstance().executeTransaction{
                                    item.deleteFromRealm()
                                }
                            })
                            .setNegativeButton("キャンセル",  { _,_ -> })
                            .show()
                }
            }


        }
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
                ViewHolder(layoutInflater.inflate(R.layout.item_user,parent,false))

        inner  class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val icon:ImageView=view.iv_icon
            val screenname: AppCompatTextView =view.tv_screenname
            val username: EmojiAppCompatTextView =view.tv_username
            val description: EmojiAppCompatTextView =view.tv_description
            val background: ConstraintLayout =view.item_user_background
        }


    }
}