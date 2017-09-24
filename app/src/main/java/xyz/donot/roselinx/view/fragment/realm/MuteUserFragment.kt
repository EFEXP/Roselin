package xyz.donot.roselinx.view.fragment.realm

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.text.emoji.widget.EmojiAppCompatTextView
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_user.view.*
import twitter4j.User
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.realm.MuteObject
import xyz.donot.roselinx.util.getDeserialized
import xyz.donot.roselinx.view.fragment.base.ARecyclerFragment


class MuteUserFragment : ARecyclerFragment(){
    val adapter by lazy { MuteUserAdapter(Realm.getDefaultInstance().where(MuteObject::class.java).notEqualTo("id",0L).findAll()) }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler.adapter=adapter

    }

     inner class MuteUserAdapter(orderedRealmCollection: OrderedRealmCollection<MuteObject>): RealmRecyclerViewAdapter<MuteObject, MuteUserAdapter.ViewHolder>(orderedRealmCollection,true){

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
                                Realm.getDefaultInstance().use {
                                    it.executeTransaction{
                                        item.deleteFromRealm()
                                    }
                                }
                            })
                            .setNegativeButton("キャンセル",  { _,_ -> })
                            .show()
                }
            }


        }
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
                ViewHolder(layoutInflater.inflate(R.layout.item_user,parent,false))

        inner  class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView) , LayoutContainer {
            val icon:ImageView=containerView.iv_icon
            val screenname: AppCompatTextView =containerView.tv_screenname
            val username: EmojiAppCompatTextView =containerView.tv_username
            val description: EmojiAppCompatTextView =containerView.tv_description
            val background: ConstraintLayout =containerView.item_user_background
        }


    }
}
