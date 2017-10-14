package xyz.donot.roselinx.customrecycler

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import xyz.donot.roselinx.ui.status.KViewHolder
import xyz.donot.roselinx.ui.util.diff.Distinguishable
import xyz.donot.roselinx.ui.util.diff.MyDiffCallback
import xyz.donot.roselinx.ui.util.diff.calculateDiff
import xyz.donot.roselinx.util.extraUtils.inflate
import xyz.donot.roselinx.util.extraUtils.logd
import kotlin.properties.Delegates


abstract class CalculableRecyclerAdapter<T : Distinguishable>(val layout:Int) : RecyclerView.Adapter<KViewHolder>() {
    private val binder = MyDiffCallback<CalculableRecyclerAdapter<T>>()
    private var recycler: RecyclerView? = null
    var onItemClick: (item: T, position: Int) -> Unit ={ _, _ -> }
    var onItemLongClick: (item: T, position: Int) -> Unit = { _, _ -> }
    var onLoadMore: () -> Unit = {  }
    var itemList: List<T> by Delegates.observable(emptyList()) { _, old, new ->
        calculateDiff(old, new).dispatchUpdatesTo(binder)
        if (binder.firstInsert==0&&(recycler?.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()==0) {
            recycler?.smoothScrollToPosition(binder.firstInsert)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)  = KViewHolder(parent.context.inflate(layout, parent, false))

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        recycler = recyclerView
        binder.bind(this@CalculableRecyclerAdapter)

    }

    override fun onBindViewHolder(holder: KViewHolder, position: Int) {
        val item=itemList[position]
        logd{"${position+1} == ${itemList.size}"}
        if (position+1==itemList.size&&itemList.size>5) { onLoadMore()}
        holder.containerView.apply {
           setOnClickListener { onItemClick(item, position) }
           setOnLongClickListener { onItemLongClick(item, position)
               true }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
