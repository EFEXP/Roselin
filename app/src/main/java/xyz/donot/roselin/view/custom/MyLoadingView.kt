package xyz.donot.roselin.view.custom

import xyz.donot.roselin.R

class MyLoadingView: MyLoadMoreView (){
    override fun getLayoutId(): Int = R.layout.item_loading

    override fun getLoadingViewId(): Int = R.id.load_more_loading_view

    override fun getLoadEndViewId(): Int = 0

    override fun getLoadFailViewId(): Int = R.id.load_more_load_fail_view


}
