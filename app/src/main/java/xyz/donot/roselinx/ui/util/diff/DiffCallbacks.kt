package xyz.donot.roselinx.ui.util.diff

import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.util.DiffUtil
import android.support.v7.util.ListUpdateCallback
import android.support.v7.widget.RecyclerView
import xyz.donot.roselinx.ui.status.KViewHolder


class DistinguishableCallback<T: Distinguishable>: DiffCallback<T>() {
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


private class Callback(
        val old: List<Distinguishable>,
        val new: List<Distinguishable>
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

fun calculateDiff(
        old: List<Distinguishable>,
        new: List<Distinguishable>,
        detectMoves: Boolean = false
): DiffUtil.DiffResult {
    return DiffUtil.calculateDiff(Callback(old, new), detectMoves)
}
