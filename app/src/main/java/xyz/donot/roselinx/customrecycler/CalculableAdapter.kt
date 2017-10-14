package xyz.donot.roselinx.customrecycler

import android.arch.paging.PagedListAdapter
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import xyz.donot.roselinx.ui.status.KViewHolder
import xyz.donot.roselinx.ui.util.diff.Distinguishable
import xyz.donot.roselinx.ui.util.diff.DistinguishableCallback
import xyz.donot.roselinx.ui.util.diff.MyDiffCallback
import xyz.donot.roselinx.util.extraUtils.inflate
import xyz.donot.roselinx.util.extraUtils.logd



abstract class CalculableAdapter<T:Distinguishable>(val layout:Int):PagedListAdapter<T, KViewHolder>(DistinguishableCallback<T>()){
    private val binder = MyDiffCallback<CalculableAdapter<T>>()
    private var recycler: RecyclerView? = null
    var onItemClick: (item: T, position: Int) -> Unit ={ _, _ -> }
    var onItemLongClick: (item: T, position: Int) -> Unit = { _, _ -> }
    var onLoadMore: () -> Unit = {  }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recycler = recyclerView
    //   binder.bind(this@CalculableTweetAdapter)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KViewHolder {
        return   if (viewType== ITEM_VIEW_TYPE_HEADER) {
            KViewHolder(mHeaderView!!)
        }
        else{
            KViewHolder(parent.context.inflate(layout, parent, false))
        }
    }

    override fun onBindViewHolder(holder: KViewHolder, position: Int) {
        if (isHeader(position)) return
        val item=getItem(position)!!
        logd{"${position+1} == $itemCount"}
        if (position+1==itemCount&&itemCount>5) { onLoadMore()}
        holder.itemView.setOnClickListener { onItemClick(item, position) }
        holder.itemView.setOnLongClickListener { onItemLongClick(item, position)
            true }
    }

    //Header
    private var mHeaderView: View?=null
    override fun getItemViewType(position: Int): Int {
        return if (isHeader(position)) {
            ITEM_VIEW_TYPE_HEADER
        }
        else ITEM_VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        var extraCount = 0
        if (mHeaderView != null) extraCount += 1
        return super.getItemCount() + extraCount
    }
    private fun isHeader(position: Int): Boolean {
        return mHeaderView != null && position == 0
    }

    fun setHeaderView(headerView: View) {
        mHeaderView = headerView
    }
    companion object {
        var ITEM_VIEW_TYPE_HEADER = 310
        var ITEM_VIEW_TYPE_ITEM = 311
    }


}


