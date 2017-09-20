package xyz.donot.roselinx.model

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import xyz.donot.roselinx.util.Regex


class UserSuggestAdapter(mContext: Context, resource: Int, val objects: List<String>) : ArrayAdapter<String>(mContext, resource, objects) {
    private var filter: UserFilter = UserFilter()
    var listener: CursorPositionListener? = null
    val suggests = ArrayList<String>()
    override fun getCount(): Int = suggests.size
    override fun getFilter(): Filter = filter
    override fun getItem(position: Int) = suggests[position]


    inner class UserFilter : Filter() {
        private val pattern = Regex.MENTION_PATTERN
        var start = 0
        var end = 0
        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults? {
            val filterResults = Filter.FilterResults()
            if (constraint != null) {
                suggests.clear()
                val cursorPosition = listener!!.currentCursorPosition()
                val m = pattern.matcher(constraint.toString())
                while (m.find()) {
                    if (m.start() < cursorPosition && cursorPosition <= m.end()) {
                        start = m.start()
                        end = m.end()
                        val tag = constraint.subSequence(m.start(), m.end()).toString()
                        suggests.addAll(objects.filter { it.toLowerCase().startsWith(tag) })
                    }
                }
            }
            filterResults.values = suggests
            filterResults.count = suggests.size
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults?) {
            if (results != null && results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }

}

interface CursorPositionListener {
    fun currentCursorPosition(): Int
}