package xyz.donot.roselinx.view.fragment

import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.donot.roselinx.R
import kotlin.properties.Delegates

open class ARecyclerFragment: AppCompatDialogFragment(){

    var recycler by Delegates.notNull<RecyclerView>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.content_a_recycler, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view?.let {
            recycler= view.findViewById(R.id.recycler)
            val dividerItemDecoration = DividerItemDecoration(recycler.context, LinearLayoutManager(activity).orientation)
            recycler.apply {
                (itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
                layoutManager = LinearLayoutManager(activity)
                addItemDecoration(dividerItemDecoration)
            }


        }



    }

}
