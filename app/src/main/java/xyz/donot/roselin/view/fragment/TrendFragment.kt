package xyz.donot.roselin.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.Trend
import xyz.donot.roselin.R
import xyz.donot.roselin.view.activity.SearchActivity
import xyz.donot.roselin.view.activity.TweetEditActivity

class TrendFragment : BaseListFragment<Trend>() {
    override fun adapterFun(): BaseQuickAdapter<Trend, BaseViewHolder> =
       TrendAdapter()
    override fun pullToRefresh(adapter: BaseQuickAdapter<Trend, BaseViewHolder>) {

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shouldLoad=false
        refresh.isEnabled=false
        adapter.setOnItemClickListener { _, _, position ->
            if (activity is SearchActivity) {
                this@TrendFragment.startActivity(Intent(context, SearchActivity::class.java).putExtra("query_text", adapter.data[position].query))
            }
           if (activity is TweetEditActivity){
               (activity as TweetEditActivity).addTrendHashtag(adapter.data[position].name)
               this@TrendFragment.dismiss()
           }
       }
    }
    override fun GetData(): MutableList<Trend>? =  twitter.getPlaceTrends(23424856).trends.asList().toMutableList()
    inner class TrendAdapter:BaseQuickAdapter<Trend,BaseViewHolder>(R.layout.item_trend)
    {
        override fun convert(helper: BaseViewHolder, item: Trend) {
            helper.setText(R.id.trend_txt,item.name)
        }
    }

}
