package xyz.donot.roselinx.ui.mention

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.LivePagedListBuilder
import android.os.Bundle
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.model.entity.MENTION_TIMELINE
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.model.entity.Tweet
import xyz.donot.roselinx.ui.base.ARecyclerFragment
import xyz.donot.roselinx.ui.dialog.getTweetDialog
import xyz.donot.roselinx.ui.status.TweetAdapter
import xyz.donot.roselinx.ui.util.extraUtils.delayed
import xyz.donot.roselinx.ui.util.getDeserialized

class MentionTimeLine : ARecyclerFragment() {
    val viewmodel: MentionViewModel by lazy { ViewModelProviders.of(this).get(MentionViewModel::class.java) }
    val adapter by lazy { TweetAdapter(activity!!) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.apply {
            twitter = arguments!!.getByteArray("twitter").getDeserialized()
            adapter.apply {
                onLoadMore = {
                    viewmodel.loadMoreData(true)
                }
                onItemClick = { (status), _ ->
                    adapter.onItemClick = { (status), _ ->
                        getTweetDialog(activity!!, this@MentionTimeLine, viewmodel.mainTwitter, status)?.show()
                    }
                }
                dataRefreshed.observe(this@MentionTimeLine, Observer {
                    refresh.setRefreshing(false)
                })
                //    setOnLoadMoreListener({ viewmodel.loadMoreData() }, recycler)
                //    setLoadMoreView(MyLoadingView())
                //     emptyView = View.inflate(activity, R.layout.item_empty, null)
                /*   addHeaderView(View.inflate(activity, R.layout.item_ad, null).apply {
                       adView.loadAd(AdRequest.Builder()
                               .setGender(AdRequest.GENDER_MALE)
                               .addTestDevice("0CF83648F3E630518CF53907939C9A8D")
                               .addTestDevice("6D38172C5A30A07095F6420BC145C497")
                               .build())
                   })*/
            }
            if (savedInstanceState == null) {
                initService()
            }
            launch(UI) {
                async {
                    LivePagedListBuilder<Int,Tweet>( RoselinDatabase.getInstance().tweetDao().getAllDataSource(MENTION_TIMELINE,twitter.id),50).build() }.await()
                        .observe(this@MentionTimeLine, Observer {
                            it?.let {
                                if (it.isEmpty())
                                    viewmodel.loadMoreData(false)
                                adapter.setList(it)
                            }
                        })
            }
            recycler.adapter = adapter
            refresh.setOnRefreshListener {
                Handler().delayed(1000, {
                    pullDown()
                })
            }
        }
        refresh.isEnabled = true

    }

}

