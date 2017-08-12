package xyz.donot.roselin.view.fragment

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.Paging
import twitter4j.ResponseList
import twitter4j.Status
import twitter4j.Twitter
import xyz.donot.roselin.extend.SafeAsyncTask
import xyz.donot.roselin.util.extraUtils.toast

class HomeTimeLineFragment :TimeLineFragment(){
    override fun loadMore(adapter: BaseQuickAdapter<Status, BaseViewHolder>) {
        val asyncTask:SafeAsyncTask<Twitter,ResponseList<Status>> = object : SafeAsyncTask<Twitter,ResponseList<Status>>() {
            override fun doTask(arg: Twitter): ResponseList<twitter4j.Status> {
                return arg.getHomeTimeline(Paging(page))
            }
            override fun onSuccess(result: ResponseList<twitter4j.Status>) {
                adapter.addData(result)
                adapter.loadMoreComplete()
            }

            override fun onFailure(exception: Exception) {
                toast(exception.localizedMessage)
            }

        }
        asyncTask.execute(twitter)
    }

    override fun pullToRefresh(adapter: BaseQuickAdapter<Status, BaseViewHolder>) {
        val asyncTask: SafeAsyncTask<Twitter, ResponseList<Status>> = object : SafeAsyncTask<Twitter, ResponseList<Status>>() {
            override fun doTask(arg: Twitter): ResponseList<twitter4j.Status> {
                return arg.getHomeTimeline(Paging(adapter.data[0].id))
            }
            override fun onSuccess(result: ResponseList<twitter4j.Status>) {
                if (result.isNotEmpty()){
                    adapter.addData(0,result)
                    recycler.smoothScrollToPosition(0)
                }

            }

            override fun onFailure(exception: Exception) {
                toast(exception.localizedMessage)
            }

        }
        asyncTask.execute(twitter)
    }


}
