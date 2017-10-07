package xyz.donot.roselinx.view.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.model.room.RoselinDatabase
import xyz.donot.roselinx.view.activity.EditTweetActivity
import xyz.donot.roselinx.view.adapter.TweetDraftAdapter
import xyz.donot.roselinx.view.fragment.base.ARecyclerFragment
import xyz.donot.roselinx.viewmodel.activity.EditTweetViewModel


class DraftFragment : ARecyclerFragment() {
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       view.apply {
            val mAdapter= TweetDraftAdapter(activity)
            mAdapter.onItemClick={item, _ ->
                if(activity is EditTweetActivity){
                    ViewModelProviders.of(activity).get(EditTweetViewModel::class.java).draft.value=item
                }
               launch {  RoselinDatabase.getInstance(activity).tweetDraftDao().delete(item) }
                this@DraftFragment.dismiss()
            }
            recycler.adapter=mAdapter

           launch (UI){
               mAdapter.itemList=  async { RoselinDatabase.getInstance(getContext()).tweetDraftDao().getAll()}.await()
           }

        }}}