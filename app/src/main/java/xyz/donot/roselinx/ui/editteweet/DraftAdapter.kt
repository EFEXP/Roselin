package xyz.donot.roselinx.ui.editteweet

import kotlinx.android.synthetic.main.item_draft.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.R
import xyz.donot.roselinx.customrecycler.CalculableTweetAdapter
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.model.entity.TweetDraft
import xyz.donot.roselinx.ui.status.KViewHolder


class TweetDraftAdapter : CalculableTweetAdapter<TweetDraft> (R.layout.item_draft){
    override fun onBindViewHolder(holder: KViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)!!
        holder.containerView.apply {
            draft_txt.text = item.text
            delete_draft.setOnClickListener {
                launch(UI) {
                    async { RoselinDatabase.getInstance().tweetDraftDao().delete(item)}.await()
                }
            }
        }
    }


}



