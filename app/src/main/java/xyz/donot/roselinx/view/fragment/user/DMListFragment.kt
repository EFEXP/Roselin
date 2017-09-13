package xyz.donot.roselinx.view.fragment.user

import android.os.Bundle
import android.view.View
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import twitter4j.DirectMessage
import twitter4j.Paging
import xyz.donot.roselinx.view.adapter.DirectMessageAdapter
import xyz.donot.roselinx.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselinx.view.custom.MyViewHolder
import xyz.donot.roselinx.view.fragment.BaseListFragment

class DMListFragment : BaseListFragment<DirectMessage>() {
    override fun adapterFun(): MyBaseRecyclerAdapter<DirectMessage, MyViewHolder> = DirectMessageAdapter()
    var page: Int = 0
        set(value) {
            field = value
        }
        get() {
            field++
            return field
        }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.getData = { twitter ->
            async(CommonPool) {
                twitter.getDirectMessages(Paging(page))
            }
        }
    }

}
