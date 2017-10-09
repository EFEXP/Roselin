package xyz.donot.roselinx.view.fragment.realm

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.content_edit_profile.*
import kotlinx.android.synthetic.main.item_user.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.R
import xyz.donot.roselinx.customrecycler.CalculableRecyclerAdapter
import xyz.donot.roselinx.model.room.MuteFilter
import xyz.donot.roselinx.model.room.RoselinDatabase
import xyz.donot.roselinx.view.adapter.KViewHolder
import xyz.donot.roselinx.view.fragment.base.ARecyclerFragment


class MuteUserFragment : ARecyclerFragment(){
    val adapter by lazy { MuteUserAdapter() }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler.adapter=adapter
        launch(UI) {
            async {
                RoselinDatabase.getInstance().muteFilterDao().getMuteUser()
            }.await()
                    .observe(
                            this@MuteUserFragment,
                            Observer {
                                it?.let {
                                    adapter.itemList = it
                                }
                            }
                    )
        }
    }

     inner class MuteUserAdapter: CalculableRecyclerAdapter< KViewHolder,MuteFilter>(){

        override fun onBindViewHolder(holder: KViewHolder, position: Int) {
            val item=itemList[position]
            val user=item.user
            holder.itemView.apply {
                tv_screenname.text="@"+user?.screenName
                Picasso.with(activity).load(user?.biggerProfileImageURLHttps).into(icon)
                tv_username.text=user?.name
                tv_description.text=user?.description
                item_user_background.setOnClickListener {
                    AlertDialog.Builder(activity)
                            .setTitle("削除しますか？")
                            .setPositiveButton("OK", {_, _ ->
                              launch {   RoselinDatabase.getInstance().muteFilterDao().delete(item) }
                            })
                            .setNegativeButton("キャンセル",  { _,_ -> })
                            .show()
                }
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int):  KViewHolder =
                KViewHolder(layoutInflater.inflate(R.layout.item_user,parent,false))
    }
}
