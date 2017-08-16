package xyz.donot.roselin.view.activity


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_twitter_detail.*
import kotlinx.android.synthetic.main.content_twitter_detail.*
import twitter4j.Query
import twitter4j.QueryResult
import twitter4j.Status
import twitter4j.Twitter
import xyz.donot.roselin.R
import xyz.donot.roselin.extend.SafeAsyncTask
import xyz.donot.roselin.util.extraUtils.logd
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.view.adapter.StatusAdapter


class TwitterDetailActivity : AppCompatActivity() {
    val  mAdapter by lazy { StatusAdapter(this@TwitterDetailActivity, arrayListOf()) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitter_detail)
        setSupportActionBar(toolbar)
        val status: Status=intent.extras.getSerializable("Status") as Status
        loadReply(status.id)
        getDiscuss(status)
        detail_recycler_view.adapter = mAdapter
        detail_recycler_view.layoutManager = LinearLayoutManager(this@TwitterDetailActivity)
    }

    fun loadReply(long: Long){
        class ConvTask:SafeAsyncTask<Twitter,Status>(){
            override fun doTask(arg: Twitter): twitter4j.Status {
                return  arg.showStatus(long)
            }

            override fun onSuccess(result: twitter4j.Status) {
                mAdapter.addData(0,result)
                val voo=result.inReplyToStatusId>0
                if(voo){
                    loadReply(result.inReplyToStatusId)
                }
            }

            override fun onFailure(exception: Exception) {

            }
        }
    ConvTask().execute(getTwitterInstance())
    }


    private fun getDiscuss(status: Status){
        val twitter by lazy { getTwitterInstance() }
        val id = status.id
        val screenname = status.user.screenName
        val query= Query("@$screenname since_id:$id")
        query.count=100
        logd("Tag",query.count.toString())

        class DiscussTask:SafeAsyncTask<Twitter,QueryResult>(){
            override fun doTask(arg: Twitter): QueryResult{
                return  twitter.search(query)
            }

            override fun onSuccess(result: QueryResult) {
                result.tweets.forEach {
                    if (it.inReplyToStatusId == id){mAdapter.addData(it)}
                }


            }

            override fun onFailure(exception: Exception) {

            }
        }
        DiscussTask().execute(getTwitterInstance())


    }
    }




