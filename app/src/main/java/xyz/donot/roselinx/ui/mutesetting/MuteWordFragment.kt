package xyz.donot.roselinx.ui.mutesetting

import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import kotlinx.android.synthetic.main.item_mute.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.R
import xyz.donot.roselinx.customrecycler.CalculableTweetAdapter
import xyz.donot.roselinx.model.entity.MuteFilter
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.ui.status.KViewHolder
import xyz.donot.roselinx.ui.base.ARecyclerFragment

class MuteWordFragment : ARecyclerFragment() {
    val adapter by lazy { MuteWordAdapter() }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler.adapter = adapter
        launch(UI) {
            async {
                RoselinDatabase.getInstance().muteFilterDao().getMuteWord()
                        .create(0, PagedList.Config.Builder().setPageSize(50).setPrefetchDistance(50).build()) }.await()
                    .observe(this@MuteWordFragment, Observer {
                        it?.let {
                            adapter.setList(it)
                        }
                    })
        }
    }
    inner class MuteWordAdapter : CalculableTweetAdapter<MuteFilter>(R.layout.item_mute) {

        override fun onBindViewHolder(holder: KViewHolder, position: Int) {
            val item =getItem(position)!!
            val text: String? = item.text
            holder.containerView.apply {
                mute_query.text = text + if (item.kichitsui == 1) "(置き換え有効)" else ""
                mute_background.setOnClickListener {
                    val tweetItem = R.array.mute
                    AlertDialog.Builder(context).setItems(tweetItem, { _, int ->
                        val selectedItem = context.resources.getStringArray(tweetItem)[int]
                        when (selectedItem) {
                            "削除" -> {
                                launch { RoselinDatabase.getInstance().muteFilterDao().delete(item)  }

                            }
                            "置き換えミュート" -> {
                                val boolean = if (item.kichitsui == 0) 1 else 0
                                val modified = item.copy(kichitsui = boolean)
                                launch { RoselinDatabase.getInstance().muteFilterDao().update(modified) }
                            }
                        }
                    }).show()
                }
            }

        }

    }
}
