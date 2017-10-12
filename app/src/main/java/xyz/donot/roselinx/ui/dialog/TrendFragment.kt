package xyz.donot.roselinx.ui.dialog

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_trend.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Trend
import twitter4j.TwitterException
import xyz.donot.roselinx.R
import xyz.donot.roselinx.Roselin
import xyz.donot.roselinx.ui.base.ARecyclerFragment
import xyz.donot.roselinx.ui.editteweet.EditTweetActivity
import xyz.donot.roselinx.ui.editteweet.EditTweetViewModel
import xyz.donot.roselinx.ui.search.SearchActivity
import xyz.donot.roselinx.ui.util.getAccount
import xyz.donot.roselinx.util.extraUtils.toast
import xyz.donot.roselinx.util.extraUtils.twitterExceptionMessage

class TrendFragment : ARecyclerFragment() {
   private val viewmodel: TrendViewModel by lazy { ViewModelProviders.of(this).get(TrendViewModel::class.java)  }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            viewmodel.apply {
                adapter.apply {
                    emptyView = View.inflate(activity, R.layout.item_empty, null)
                    setOnItemClickListener { _, _, position ->
                        if (activity is SearchActivity) {
                            activity.startActivity(Intent(context, SearchActivity::class.java).putExtra("query_text", viewmodel.adapter.data[position].query))
                        }
                        if (activity is EditTweetActivity) {
                            ViewModelProviders.of(activity).get(EditTweetViewModel::class.java).hashtag.value = viewmodel.adapter.data[position].name
                            this@TrendFragment.dismiss()
                        }
                    }
                }
                recycler.adapter = adapter
                if (savedInstanceState == null)
                    loadMoreData()

                exception.observe(this@TrendFragment, Observer {
                    it?.let {
                        adapter.emptyView = View.inflate(activity, R.layout.item_no_content, null)
                    }
                })
            }
    }
}

class TrendAdapter : BaseQuickAdapter<Trend, BaseViewHolder>(R.layout.item_trend) {
    override fun convert(helper: BaseViewHolder, item: Trend) {
        helper.itemView.trend_txt.text = item.name
    }
}
class TrendViewModel(application: Application) : AndroidViewModel(application) {
    val exception = MutableLiveData<TwitterException>()
    private val mainTwitter by lazy { getAccount() }
    val adapter by lazy { TrendAdapter() }
    fun loadMoreData() {
        launch(UI) {
            try {
                val result = async(CommonPool){mainTwitter.account.getPlaceTrends(23424856).trends.asList()}.await()
                if (!result.isEmpty()) {
                    adapter.addData(result)
                }
            } catch (e: TwitterException) {
                adapter.loadMoreFail()
                exception.value = e
                getApplication<Roselin>().toast(twitterExceptionMessage(e))
            }
        }
    }

}