package xyz.donot.roselinx.service

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import twitter4j.*
import xyz.donot.roselinx.util.StreamCreateUtil
import xyz.donot.roselinx.util.getSerialized
import xyz.donot.roselinx.util.getTwitterInstance

class SearchStreamService : IntentService("SearchStreamService") {
    private val twitter by lazy {  getTwitterInstance() }
    private val stream: TwitterStream by lazy {  TwitterStreamFactory().getInstance(twitter.authorization) }
    private var query:String? =null

    override fun onHandleIntent(intent: Intent) {
        query=intent.getStringExtra("query_text")
        handleActionStream(query)
    }
    private fun handleActionStream(query:String?){
        val filter= FilterQuery().apply { track(query)  }
        StreamCreateUtil.addStatusListener(stream,MyStreamAdapter())
        stream.filter(filter)
    }

    inner class MyStreamAdapter: UserStreamAdapter(){
        override fun onStatus(status: Status) {
            super.onStatus(status)
            if (!status.isRetweet){
            LocalBroadcastManager.getInstance(this@SearchStreamService).sendBroadcast(Intent(query).putExtra("Status", status.getSerialized()))
        }}
    }
}
