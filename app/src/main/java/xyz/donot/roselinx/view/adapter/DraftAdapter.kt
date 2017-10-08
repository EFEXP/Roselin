package xyz.donot.roselinx.view.adapter

import android.content.Context
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_draft.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.R
import xyz.donot.roselinx.customrecycler.CalculableRecyclerAdapter
import xyz.donot.roselinx.model.room.RoselinDatabase
import xyz.donot.roselinx.model.room.TweetDraft
import xyz.donot.roselinx.util.extraUtils.inflater


class TweetDraftAdapter(private val androidContext: Context) : CalculableRecyclerAdapter<TweetDraftAdapter.DraftViewHolder,TweetDraft> (){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftViewHolder = DraftViewHolder(androidContext.inflater.inflate(R.layout.item_draft, parent, false))

    override fun onBindViewHolder(holder: DraftViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = itemList[position]
        holder.apply {
            draftText.text = item.text
            deleteDraft.setOnClickListener {
                launch(UI) {
                     async { RoselinDatabase.getInstance().tweetDraftDao().delete(item)}.await()
                }
            }
        }
    }
    inner class DraftViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var draftText: TextView = view.draft_txt
        var deleteDraft: AppCompatImageButton = view.delete_draft
    }
}



