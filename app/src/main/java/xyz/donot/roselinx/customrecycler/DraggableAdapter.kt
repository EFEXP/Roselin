package xyz.donot.roselinx.customrecycler

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.ViewGroup
import xyz.donot.roselinx.util.extraUtils.inflate
import xyz.donot.roselinx.ui.status.KViewHolder
import java.util.*


abstract class DraggableAdapter<T : Diffable>(layout:Int) : CalculableRecyclerAdapter<T>(layout) {

    var onMoveEnd:()->Unit={}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = KViewHolder(parent.context.inflate(layout, parent, false))

    fun onItemDragEnd(){
        onMoveEnd()
    }
    fun onItemDragMoving(source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) {
        val from =source.adapterPosition
        val to =target.adapterPosition
        if (inRange(from) && inRange(to)) {
            if (from < to) {
                for (i in from until to) {
                    Collections.swap(itemList, i, i + 1)
                }
            } else {
                for (i in from downTo to + 1) {
                    Collections.swap(itemList, i, i - 1)
                }
            }
            notifyItemMoved(source.adapterPosition, target.adapterPosition)
        }

    }

    private fun inRange(position: Int): Boolean {
        return position >= 0 && position <itemList.size
    }

}


class ItemDragAndSwipeCallback(private val mAdapter: DraggableAdapter<*>) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
                return makeFlag(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.RIGHT) or makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.DOWN or ItemTouchHelper.UP)

    }

    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
            mAdapter.onItemDragEnd()
    }

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        mAdapter.onItemDragMoving(viewHolder, target)
        return true

    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {

    }


}