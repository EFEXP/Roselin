package xyz.donot.roselin.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.activity_oauth.*
import kotlinx.android.synthetic.main.content_main.*
import twitter4j.Paging
import twitter4j.ResponseList
import twitter4j.Status
import twitter4j.Twitter
import xyz.donot.roselin.R
import xyz.donot.roselin.extend.SafeAsyncTask
import xyz.donot.roselin.util.extraUtils.getActivity
import xyz.donot.roselin.util.extraUtils.intent
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.util.haveToken
import xyz.donot.roselin.view.adapter.StatusAdapter
import xyz.donot.roselin.view.custom.MyLoadingView


class MainActivity : AppCompatActivity() {
    val twitter by lazy { getTwitterInstance() }
    var page: Int = 0
        get() {
            field++
            return field
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        if (!haveToken()) {
            startActivity(intent<OauthActivity>())
            this.finish()
        }
        else{
            recycler.layoutManager = LinearLayoutManager(this)
            val adapter=StatusAdapter(getActivity(), mutableListOf())
            adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT)
            adapter.setOnLoadMoreListener({  loadMore(adapter) },recycler)
            adapter.setLoadMoreView(MyLoadingView())
            adapter.emptyView=View.inflate(this@MainActivity, R.layout.item_empty,null)
            recycler.adapter=adapter

            loadMore(adapter)


}}

fun loadMore(adapter: StatusAdapter){
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
}
