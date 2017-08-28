package xyz.donot.roselin.view.fragment.realm

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.chad.library.adapter.base.BaseViewHolder
import com.squareup.picasso.Picasso
import io.realm.OrderedRealmCollection
import io.realm.Realm
import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.User
import xyz.donot.roselin.R
import xyz.donot.roselin.model.realm.DBMute
import xyz.donot.roselin.util.getDeserialized
import xyz.donot.roselin.view.custom.MyBaseAdapter


class MuteUserFragment : AppCompatDialogFragment(){
    val realm: Realm by lazy { Realm.getDefaultInstance() }
    val adapter by lazy {MuteUserAdater(realm.where(DBMute::class.java).notEqualTo("id",0L).findAll()) }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dividerItemDecoration = DividerItemDecoration(context,LinearLayoutManager(context).orientation)
        recycler.addItemDecoration(dividerItemDecoration)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter=adapter
        refresh.isEnabled=false
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.content_base_fragment, container, false)
    inner class MuteUserAdater(orderedRealmCollection: OrderedRealmCollection<DBMute>): MyBaseAdapter<DBMute,BaseViewHolder>(orderedRealmCollection,R.layout.item_user){
        override fun convert(helper: BaseViewHolder, item: DBMute) {
            val user = item.user?.getDeserialized<User>()
            helper.apply {
                setText(R.id.tv_screenname, "@" + user?.screenName)
                setText(R.id.tv_username, user?.name)
                setText(R.id.tv_description, user?.description)
                Picasso.with(activity).load(user?.biggerProfileImageURLHttps).into( getView<ImageView>(R.id.iv_icon))
                getView<View>(R.id.item_user_background).setOnClickListener {
                    AlertDialog.Builder(activity)
                            .setTitle("削除しますか？")
                            .setPositiveButton("OK", { dialog, _ ->
                                Realm.getDefaultInstance().executeTransaction {
                                    item.deleteFromRealm()
                                }
                            })
                            .setNegativeButton("キャンセル", { dialog, whichButton -> })
                            .show()
                }
            }

        }



    }
}
