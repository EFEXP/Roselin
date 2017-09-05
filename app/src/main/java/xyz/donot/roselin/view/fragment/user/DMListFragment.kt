package xyz.donot.roselin.view.fragment.user

import android.os.Bundle
import android.view.View
import twitter4j.DirectMessage
import twitter4j.Paging
import xyz.donot.roselin.view.adapter.DirectMessageAdapter
import xyz.donot.roselin.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselin.view.custom.MyViewHolder
import xyz.donot.roselin.view.fragment.BaseListFragment

class DMListFragment: BaseListFragment<DirectMessage>()
{
	override fun adapterFun(): MyBaseRecyclerAdapter<DirectMessage, MyViewHolder> =DirectMessageAdapter()
	var page: Int = 0
		set(value) {
			field=value
		}
		get() {
			field++
			return field
		}

	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
	}

	override fun GetData(): MutableList<DirectMessage>? = twitter.getDirectMessages(Paging(page))

}
