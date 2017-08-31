package xyz.donot.roselin.view.activity


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_twitter_detail.*
import kotlinx.android.synthetic.main.content_a_recycler.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Query
import twitter4j.Status
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.logd
import xyz.donot.roselin.util.extraUtils.tExceptionToast
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.view.adapter.StatusAdapter


class TwitterDetailActivity : AppCompatActivity() {
    val  mAdapter by lazy { StatusAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitter_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val status: Status=intent.extras.getSerializable("Status") as Status
        loadReply(status.id)
        getDiscuss(status)
        a_recycler_view.adapter = mAdapter
        a_recycler_view.layoutManager = LinearLayoutManager(this@TwitterDetailActivity)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


    fun loadReply(long: Long){
        launch(UI){
            try {
                val result= async(CommonPool){ getTwitterInstance().showStatus(long)}.await()
                mAdapter.addData(0,result)
                val voo=result.inReplyToStatusId>0
                if(voo){
                    loadReply(result.inReplyToStatusId)
                }
            } catch (e: Exception) {
                tExceptionToast(e)
            }
        }
    }


    private fun getDiscuss(status: Status){
        val twitter by lazy { getTwitterInstance() }
        val screenname = status.user.screenName
        val query= Query("@$screenname since_id:${status.id}")
        query.count=100
        logd("ReplyQuery",query.count.toString())
        launch(UI){
            try {
                val result= async(CommonPool){ twitter.search(query)}.await()
                for (tweet in result.tweets){
                    if (tweet.inReplyToStatusId == status.id){mAdapter.addData(tweet)}
                }

            } catch (e: Exception) {
              tExceptionToast(e)
            }
        }
    }

    }





