package xyz.donot.roselinx.customrecycler

import android.support.v7.util.DiffUtil
import android.support.v7.util.ListUpdateCallback
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import xyz.donot.roselinx.util.extraUtils.logd
import kotlin.properties.Delegates


abstract class CalculableRecyclerAdapter<VH : RecyclerView.ViewHolder, T : Diffable> : RecyclerView.Adapter<VH>() {
    internal val binder = MyCallback<VH, CalculableRecyclerAdapter<VH, T>>()
    private var recycler: RecyclerView? = null
    var onItemClick: (item: T, position: Int) -> Unit ={ _, _ -> }
    var onItemLongClick: (item: T, position: Int) -> Unit = { _, _ -> }
    var onLoadMore: () -> Unit = {  }

    var itemList: List<T> by Delegates.observable(ArrayList()) { _, old, new ->
        calculateDiff(old, new).dispatchUpdatesTo(binder)
        if (binder.firstInsert==0&&(recycler?.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()==0) {
            recycler?.smoothScrollToPosition(binder.firstInsert)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        recycler = recyclerView
        binder.bind(this@CalculableRecyclerAdapter)

    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item=itemList[position]
        logd{"${position+1} == ${itemList.size}"}
        if (position+1==itemList.size&&itemList.size>5) { onLoadMore()}
        holder.itemView.setOnClickListener { onItemClick(item, position) }
        holder.itemView.setOnLongClickListener { onItemLongClick(item, position)
            true }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
//InterFace
interface Diffable {
    // otherと同じIDを持つかどうか
    fun isTheSame(other: Diffable): Boolean

    // otherと完全一致するかどうか
    fun isContentsTheSame(other: Diffable): Boolean = equals(other)
}

private class Callback(
        val old: List<Diffable>,
        val new: List<Diffable>
) : DiffUtil.Callback() {
    override fun getOldListSize() = old.size
    override fun getNewListSize() = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].isTheSame(new[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].isContentsTheSame(new[newItemPosition])
    }
}

internal class MyCallback<VH : RecyclerView.ViewHolder, Adapter : RecyclerView.Adapter<VH>> : ListUpdateCallback {
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


fun calculateDiff(
        old: List<Diffable>,
        new: List<Diffable>,
        detectMoves: Boolean = false
): DiffUtil.DiffResult {
    return DiffUtil.calculateDiff(Callback(old, new), detectMoves)
}