package xyz.donot.roselin.view.custom


import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import io.realm.OrderedRealmCollection
import io.realm.OrderedRealmCollectionChangeListener
import io.realm.RealmModel
import io.realm.RealmResults

abstract class MyBaseAdapter<T:RealmModel,S:BaseViewHolder>(orderedRealmCollection: OrderedRealmCollection<T>,layout: Int) : BaseQuickAdapter<T, S>(layout){
    private val adapterData: OrderedRealmCollection<T> =orderedRealmCollection

    private var listener: OrderedRealmCollectionChangeListener<RealmResults<T>> =  OrderedRealmCollectionChangeListener { _, changeSet ->
        if (changeSet == null) {
            notifyDataSetChanged()
            return@OrderedRealmCollectionChangeListener
        }
        val deletions = changeSet.deletionRanges
        for (i in deletions.indices.reversed()) {
            val range = deletions[i]
            notifyItemRangeRemoved(range.startIndex, range.length)
        }
        val insertions = changeSet.insertionRanges
        for (range in insertions) {
            notifyItemRangeInserted(range.startIndex, range.length)
        }
        val modifications = changeSet.changeRanges
        for (range in modifications) {
            notifyItemRangeChanged(range.startIndex, range.length)
        }
    }
    override fun getItemId(index: Int): Long = index.toLong()
    override fun getItemCount(): Int =  adapterData.size
    override fun getItem(index: Int): T =  adapterData[index]
    override fun getData(): OrderedRealmCollection<T> = adapterData
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (isDataValid()) {
            addListener(adapterData)
        }
        addData(adapterData)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        if ( isDataValid()) {
            removeListener(adapterData)
        }
    }

    private fun addListener(@NonNull data: OrderedRealmCollection<T>) =
            if (data is RealmResults<T>) {
                data.addChangeListener(listener)
            } else {
                throw IllegalArgumentException("RealmCollection not supported: " + data.javaClass)
            }
    private fun removeListener(@NonNull data: OrderedRealmCollection<T>) =
            if (data is RealmResults<T>) {
                data.removeChangeListener(listener)
            }else {
                throw IllegalArgumentException("RealmCollection not supported: " + data.javaClass)
            }

    private fun isDataValid(): Boolean = adapterData.isValid
}