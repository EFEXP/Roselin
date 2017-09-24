package xyz.donot.roselinx.view.fragment.user

import android.os.Bundle
import android.view.View
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import twitter4j.DirectMessage
import twitter4j.Paging
import xyz.donot.roselinx.view.adapter.DirectMessageAdapter
import xyz.donot.roselinx.view.fragment.base.BaseListFragment

class DMListFragment : BaseListFragment<DirectMessage>() {
    var page: Int = 0
        get() {
            field++
            return field
        }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState==null)
            viewmodel.adapter= DirectMessageAdapter()
        viewmodel.getData = { twitter ->
            async(CommonPool) {
                twitter.getDirectMessages(Paging(page))
            }
        }
    }

}
