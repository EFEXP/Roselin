package xyz.donot.roselinx.ui.editteweet

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.LivePagedListBuilder
import android.os.Bundle
import android.view.View
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.model.entity.TweetDraft
import xyz.donot.roselinx.ui.base.ARecyclerFragment


class DraftFragment : ARecyclerFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            val mAdapter = TweetDraftAdapter()
            mAdapter.onItemClick = { item, _ ->
                if (activity is EditTweetActivity) {
                    ViewModelProviders.of(activity!!).get(EditTweetViewModel::class.java).draft.value = item
                }
                launch { RoselinDatabase.getInstance().tweetDraftDao().delete(item) }
                this@DraftFragment.dismiss()
            }
            recycler.adapter = mAdapter
            launch(UI) {
                async { LivePagedListBuilder<Int, TweetDraft>(RoselinDatabase.getInstance().tweetDraftDao().getAllLiveData(),50).build() }.await()
                        .observe(this@DraftFragment, Observer {
                            it?.let {
                               mAdapter.setList(it)
                            }
                        })
            }
        }
    }
}

