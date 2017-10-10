package xyz.donot.roselinx.customrecycler

import android.arch.paging.PagedListAdapter
import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.util.ListUpdateCallback
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import xyz.donot.roselinx.util.extraUtils.inflate
import xyz.donot.roselinx.util.extraUtils.logd
import xyz.donot.roselinx.view.adapter.KViewHolder

abstract class CalculableTweetAdapter<T:Diffable>(val layout:Int):PagedListAdapter<T,KViewHolder>(DiffableCallback<T>()){
    private val binder = MyDiffCallback<CalculableTweetAdapter<T>>()
    private var recycler: RecyclerView? = null
    var onItemClick: (item: T, position: Int) -> Unit ={ _, _ -> }
    var onItemLongClick: (item: T, position: Int) -> Unit = { _, _ -> }
    var onLoadMore: () -> Unit = {  }



    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recycler = recyclerView
       binder.bind(this@CalculableTweetAdapter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = KViewHolder(parent.context.inflate(layout,parent,false))

    override fun onBindViewHolder(holder:KViewHolder, position: Int) {
        val item=getItem(position)!!
        logd{"${position+1} == $itemCount"}
        if (position+1==itemCount&&itemCount>5) { onLoadMore()}
        holder.itemView.setOnClickListener { onItemClick(item, position) }
        holder.itemView.setOnLongClickListener { onItemLongClick(item, position)
            true }
    }

}

class DiffableCallback<T:Diffable>:DiffCallback<T>() {
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return   oldItem.isTheSame(newItem)
    }

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.isContentsTheSame(newItem)
    }

}


internal class MyDiffCallback<Adapter: RecyclerView.Adapter<KViewHolder>> : ListUpdateCallback {
    var firstInsert = -1
    var adapter: Adapter? = null
    fun bind(adapter: Adapter) {
        this.adapter = adapter
    }

    override fun onChanged(position: Int, count: Int, payload: Any) {
        adapter!!.notifyItemRangeChanged(position, count, payload)
    }

    override fun onInserted(position: Int, count: Int) {
        if (firstInsert == -1 || firstInsert > position) {
            firstInsert = position
        }
        adapter!!.notifyItemRangeInserted(position, count)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        adapter!!.notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRemoved(position: Int, count: Int) {
        adapter!!.notifyItemRangeRemoved(position, count)
    }
}