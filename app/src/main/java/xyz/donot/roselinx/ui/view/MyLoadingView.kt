package xyz.donot.roselinx.ui.view

import com.chad.library.adapter.base.loadmore.LoadMoreView
import xyz.donot.roselinx.R

class MyLoadingView: LoadMoreView(){
    override fun getLayoutId(): Int = R.layout.item_loading

    override fun getLoadingViewId(): Int = R.id.load_more_loading_view

    override fun getLoadEndViewId(): Int = 0

    override fun getLoadFailViewId(): Int = R.id.load_more_load_fail_view


}
