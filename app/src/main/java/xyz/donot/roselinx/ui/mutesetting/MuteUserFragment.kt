package xyz.donot.roselinx.ui.mutesetting

import android.arch.lifecycle.Observer
import android.arch.paging.LivePagedListBuilder
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.content_edit_profile.*
import kotlinx.android.synthetic.main.item_user.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.R
import xyz.donot.roselinx.customrecycler.CalculableAdapter
import xyz.donot.roselinx.model.entity.MuteFilter
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.ui.base.ARecyclerFragment
import xyz.donot.roselinx.ui.status.KViewHolder


class MuteUserFragment : ARecyclerFragment(){
    val adapter by lazy { MuteUserAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler.adapter=adapter
        launch(UI) {
            async {
                LivePagedListBuilder<Int,MuteFilter>( RoselinDatabase.getInstance().muteFilterDao().getMuteUser(),50).build() }.await()
                    .observe(this@MuteUserFragment, Observer {
                        it?.let {
                            adapter.setList(it)
                        }
                    })
        }
    }

     inner class MuteUserAdapter: CalculableAdapter<MuteFilter>(R.layout.item_user){

        override fun onBindViewHolder(holder: KViewHolder, position: Int) {
            val item=getItem(position)!!
            val user=item.user
            holder.itemView.apply {
                tv_screenname.text="@"+user?.screenName
                Picasso.with(activity).load(user?.biggerProfileImageURLHttps).into(icon)
                tv_username.text=user?.name
                tv_description.text=user?.description
                item_user_background.setOnClickListener {
                    AlertDialog.Builder(activity!!)
                            .setTitle("削除しますか？")
                            .setPositiveButton("OK", {_, _ ->
                              launch {   RoselinDatabase.getInstance().muteFilterDao().delete(item) }
                            })
                            .setNegativeButton("キャンセル",  { _,_ -> })
                            .show()
                }
            }
        }

    }
}
