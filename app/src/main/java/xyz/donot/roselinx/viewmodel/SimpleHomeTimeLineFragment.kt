package xyz.donot.roselinx.viewmodel

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.content_a_recycler.*
import xyz.donot.roselinx.util.extraUtils.hide
import xyz.donot.roselinx.util.extraUtils.show
import xyz.donot.roselinx.util.getDeserialized
import xyz.donot.roselinx.util.getTwitterInstance
import xyz.donot.roselinx.view.fragment.ARecyclerFragment
import xyz.donot.roselinx.viewmodel.fragment.SimpleHomeViewModel


class SimpleHomeTimeLineFragment : ARecyclerFragment(), LifecycleRegistryOwner {
    lateinit var viewmodel: SimpleHomeViewModel
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel = ViewModelProviders.of(this@SimpleHomeTimeLineFragment).get(SimpleHomeViewModel::class.java)
        recycler.adapter = viewmodel.adapter
        viewmodel.twitter = if (arguments != null && arguments.containsKey("twitter")) {
            arguments.getByteArray("twitter").getDeserialized()
        } else getTwitterInstance()
        viewmodel.loadMore()
        viewmodel.isLoading.observe(this, Observer {
            it?.let {
               if (it) load_progress.show()  else load_progress.hide()
            }
        })

    }

    private val life by lazy { LifecycleRegistry(this) }
    override fun getLifecycle(): LifecycleRegistry {
        return life
    }

}