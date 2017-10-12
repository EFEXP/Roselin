package xyz.donot.roselinx.ui.base

import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_base_fragment.*
import xyz.donot.roselinx.R

open class ARecyclerFragment : AppCompatDialogFragment() {


    lateinit var recycler: RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.content_base_fragment, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view?.let {
                recycler = view.findViewById(R.id.recycler)
                val dividerItemDecoration = DividerItemDecoration(recycler.context, LinearLayoutManager(activity).orientation)
                recycler.apply {
                    (itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
                    layoutManager = LinearLayoutManager(activity)
                    addItemDecoration(dividerItemDecoration)
                }
                refresh.isEnabled = false
        }
    }
    fun scrollRecycler(position: Int) {
        recycler.smoothScrollToPosition(position)
    }
    fun reselect() = recycler.smoothScrollToPosition(0)

}
