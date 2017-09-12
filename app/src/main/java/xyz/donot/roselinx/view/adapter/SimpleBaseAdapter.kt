package xyz.donot.roselinx.view.adapter

import android.support.annotation.IntRange
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import xyz.donot.roselinx.util.extraUtils.inflate
import kotlin.properties.Delegates

abstract class SimpleBaseAdapter<T>(val data: ArrayList<T>, val layout: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var itemClickListener: OnItemClickListener? = null
    var onLoadMore: LoadMoreListener? = null
    private var rootView: View by Delegates.notNull()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindJudge(position)
    }

    private fun onBindJudge(position: Int) {
        Log.d("Loadmore", "${position + 1} >${data.size - 1}" + "" + (position + 1 > data.size - 1).toString())
        if (position + 1 > data.size - 1)
            onLoadMore?.onLoadMore()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = SimpleViewHolder(parent.context.inflate(layout, parent, false))
        rootView = holder.itemView
        rootView.setOnClickListener { itemClickListener?.onItemClick(holder.layoutPosition) }
        return holder
    }

    //count
    override fun getItemCount(): Int = data.size

    //Trueでo1.createdAt > o2.createdAt？
    private inner class SimpleViewHolder(view: View) : RecyclerView.ViewHolder(view)

    //Interface
    interface LoadMoreListener {
        fun onLoadMore()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    /* //SortedCallback
    fun calcList(newList: List<T>){
      val result=  DiffUtil.calculateDiff(DiffCallBack(data,newList))
        result.dispatchUpdatesTo(this)
    }
    abstract fun areSame(item1: T, item2: T): Boolean
    abstract fun areSameContent(item1: T, item2: T): Boolean
    private inner class DiffCallBack(private val oldList: List<T>, private val newList: List<T>) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areSame(oldList[oldItemPosition], newList[newItemPosition])
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areSameContent(oldList[oldItemPosition], newList[newItemPosition])
        }

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
    }
*/

    //region Description
    fun addData(@IntRange(from = 0) position: Int, data_: T) {
        data.add(position, data_)
        notifyItemInserted(position)
    }

    fun setData(replaceData: T, replacedData: T) {
        val i = data.indexOf(replacedData)
        data[i] = replaceData
        notifyItemChanged(i)
    }
    fun addData(data_: T) {
        data.add(data_)
        notifyItemInserted(data.size  )
    }


    fun remove(@IntRange(from = 0) position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    fun remove(shouldRemove: T) {
        val i = data.indexOf(shouldRemove)
        data.removeAt(i)
        notifyItemChanged(i  )
    }


    fun addData(@IntRange(from = 0) position: Int, newData: Collection<T>) {
        data.addAll(position, newData)
        notifyItemRangeInserted(position  , newData.size)
    }


    fun addData(newData: Collection<T>) {
        data.addAll(newData)
        notifyItemRangeInserted(data.size - newData.size  , newData.size)
    }
    //endregion

}

