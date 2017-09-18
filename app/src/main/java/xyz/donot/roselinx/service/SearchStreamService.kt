package xyz.donot.roselinx.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import twitter4j.*
import xyz.donot.roselinx.util.StreamCreateUtil
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.util.getTwitterInstance

class SearchStreamService : IntentService("SearchStreamService") {
    private val twitter by lazy { getTwitterInstance() }
    private val id by lazy { getMyId() }
    private val stream: TwitterStream by lazy { TwitterStreamFactory().getInstance(twitter.authorization) }
    private var query: String? = null

    override fun onHandleIntent(intent: Intent) {
        // query=intent.getStringExtra("query_text")
        handleActionStream("114514")
    }

    private fun handleActionStream(query: String?) {
        val filter = FilterQuery().apply { track(query) }
        StreamCreateUtil.addStatusListener(stream, MyStreamAdapter())
        stream.filter(filter)
        Log.w("114514","Start 114514 Service")
    }

    inner class MyStreamAdapter : UserStreamAdapter() {
        override fun onStatus(status: Status) {
            super.onStatus(status)
            if (!status.isRetweet && status.user.id != id&&(status.source.contains("iPhone")||status.source.contains("Android")||status.source.contains("Web"))) {
                Log.d("114514",status.user.screenName+"の"+status.text.toString())

                launch(UI){
                    try {
                     async(CommonPool){
                        delay(1000)
                        twitter.updateStatus("ンン！@${status.user.screenName} が114514って言ったゾ!!!!! やっぱ好きなんすね～^")}.await()
                    }
                    catch (e:Exception){
                        Log.w("114514",e.localizedMessage)
                    }
                }
                //   LocalBroadcastManager.getInstance(this@SearchStreamService).sendBroadcast(Intent(query).putExtra("Status", status.getSerialized()))

            }
        }
    }
}
