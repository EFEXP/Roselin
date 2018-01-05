package xyz.donot.roselinx.ui.detailtweet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Query
import twitter4j.Status
import xyz.donot.roselinx.R
import xyz.donot.roselinx.ui.base.ARecyclerFragment
import xyz.donot.roselinx.ui.dialog.getTweetDialog
import xyz.donot.roselinx.ui.status.StatusAdapter
import xyz.donot.roselinx.ui.util.extraUtils.logd
import xyz.donot.roselinx.ui.util.getAccount


class ConversationFragment : ARecyclerFragment() {
    val status by lazy { arguments?.getSerializable("status") as Status }
    val adapter by lazy { StatusAdapter() }
    val account by lazy { getAccount() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.addData(status)
        if (status.inReplyToStatusId > 0)
            if (savedInstanceState == null) {
                loadReply(status.inReplyToStatusId)
                getDiscuss(status)
            }
        recycler.adapter = adapter
        //   view.recycler.layoutManager = LinearLayoutManager(activity)
        //クリックリスナー
        adapter.setOnItemClickListener { adapter, _, position ->
            val status = adapter.data[position] as Status
            getTweetDialog(activity!!, this, account, status)?.show()
        }
        //クリックリスナーEnd
        adapter.emptyView = View.inflate(activity, R.layout.item_empty, null)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.content_base_fragment, container, false)


    private fun loadReply(long: Long) {
        launch(UI) {
            try {
                val result = async(CommonPool) {account.account.showStatus(long) }.await()
                adapter.addData(0, result)
                val voo = result.inReplyToStatusId > 0
                if (voo) {
                    loadReply(result.inReplyToStatusId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun getDiscuss(status: Status) {
        val twitter by lazy { getAccount() }
        val query = Query("to:" + status.user.screenName)
        query.count = 100
        context!!.logd { query.count.toString() }
        launch(UI) {
            try {
                val result = async(CommonPool) { twitter.account.search(query) }.await()
                result.tweets
                        .filter { it.inReplyToStatusId == status.id }
                        .forEach { adapter.addData(it) }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
