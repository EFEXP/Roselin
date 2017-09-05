package xyz.donot.roselinx.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.Trend
import xyz.donot.roselinx.R
import xyz.donot.roselinx.view.activity.SearchActivity
import xyz.donot.roselinx.view.activity.TweetEditActivity
import xyz.donot.roselinx.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselinx.view.custom.MyViewHolder

class TrendFragment : BaseListFragment<Trend>() {
    override fun adapterFun(): MyBaseRecyclerAdapter<Trend, MyViewHolder> =
       TrendAdapter()
    override fun pullToRefresh(adapter:MyBaseRecyclerAdapter<Trend, MyViewHolder>) = Unit
    override fun onCreate(savedInstanceState: Bundle?) = super.onCreate(savedInstanceState)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refresh.isEnabled=false
        adapter.setEnableLoadMore(false)
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
    override fun GetData(): MutableList<Trend>? =
            twitter.getPlaceTrends(23424856).trends.asList().toMutableList()

    inner class TrendAdapter:MyBaseRecyclerAdapter<Trend,MyViewHolder>(R.layout.item_trend)
    {
        override fun convert(helper: MyViewHolder, item: Trend,position:Int) {
            helper.setText(R.id.trend_txt,item.name)
        }
    }

}
