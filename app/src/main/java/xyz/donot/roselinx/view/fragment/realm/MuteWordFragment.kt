package xyz.donot.roselinx.view.fragment.realm

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_mute.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.R
import xyz.donot.roselinx.customrecycler.CalculableRecyclerAdapter
import xyz.donot.roselinx.model.room.MuteFilter
import xyz.donot.roselinx.model.room.RoselinDatabase
import xyz.donot.roselinx.view.fragment.base.ARecyclerFragment

class MuteWordFragment : ARecyclerFragment() {
    val adapter by lazy { MuteWordAdapter() }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler.adapter = adapter
        launch(UI) {
            async {
                RoselinDatabase.getInstance().muteFilterDao().getMuteWord()
            }.await()
                    .observe(
                            this@MuteWordFragment,
                            Observer {
                                it?.let {
                                    adapter.itemList = it
                                }
                            }
                    )
        }
    }
    inner class MuteWordAdapter : CalculableRecyclerAdapter<MuteWordAdapter.ViewHolder, MuteFilter>() {
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = itemList[position]
            val text: String? = item.text
            holder.apply {
                mute.text = text + if (item.kichitsui == 1) "(置き換え有効)" else ""
                background.setOnClickListener {
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

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
                ViewHolder(layoutInflater.inflate(R.layout.item_mute, parent, false))

        inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
            val mute: TextView = containerView.mute_query
            val background: LinearLayout = containerView.mute_background

        }


    }
}
