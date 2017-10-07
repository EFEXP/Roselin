package xyz.donot.roselinx.customrecycler

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import kotlin.properties.Delegates

abstract class CalculableRecyclerAdapter<VH : RecyclerView.ViewHolder, T : Diffable> : RecyclerView.Adapter<VH>() {
    var onItemClick: (item: T, position: Int) -> Unit = { x, y -> }

    var itemList: List<T> by Delegates.observable(emptyList()) { _, old, new ->
            calculateDiff(old, new).dispatchUpdatesTo(this@CalculableRecyclerAdapter)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.setOnClickListener { onItemClick(itemList[position], position) }
    }


    override fun getItemCount(): Int {
        return itemList.size
    }
}

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

fun calculateDiff(
        old: List<Diffable>,
        new: List<Diffable>,
        detectMoves: Boolean = false
): DiffUtil.DiffResult {
    return DiffUtil.calculateDiff(Callback(old, new), detectMoves)
}