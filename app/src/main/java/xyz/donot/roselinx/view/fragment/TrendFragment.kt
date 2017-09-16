package xyz.donot.roselinx.view.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_trend.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import twitter4j.Trend
import xyz.donot.roselinx.R
import xyz.donot.roselinx.view.activity.EditTweetActivity
import xyz.donot.roselinx.view.activity.SearchActivity
import xyz.donot.roselinx.viewmodel.EditTweetViewModel

class TrendFragment : BaseListFragment<Trend>() {
    override val adapterx by lazy { TrendAdapter() }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.adapter.setEnableLoadMore(false)
        viewmodel.adapter.setOnItemClickListener { _, _, position ->
            if (activity is SearchActivity) {
                this@TrendFragment.startActivity(Intent(context, SearchActivity::class.java).putExtra("query_text", viewmodel.adapter.data[position].query))
            }
            if (activity is EditTweetActivity) {
                ViewModelProviders.of(activity).get(EditTweetViewModel::class.java).hashtag.value = viewmodel.adapter.data[position].name
                this@TrendFragment.dismiss()
            }
        }

        viewmodel.getData = { twitter ->
            async(CommonPool) {
                twitter.getPlaceTrends(23424856).trends.asList()
            }
        }
    }


    inner class TrendAdapter : BaseQuickAdapter<Trend, BaseViewHolder>(R.layout.item_trend) {
        override fun convert(helper: BaseViewHolder, item: Trend) {
            trend_txt.text = item.name
        }
    }

}
