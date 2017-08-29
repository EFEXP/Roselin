package xyz.donot.roselin.view.fragment.status

import twitter4j.Paging
import twitter4j.Status
import twitter4j.User
import xyz.donot.roselin.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselin.view.custom.MyViewHolder

class FavoriteTimeLine:TimeLineFragment(){
    val user by lazy {arguments.getSerializable("user") as User }
    override fun pullToRefresh(adapter: MyBaseRecyclerAdapter<Status, MyViewHolder>) {

    }

    override fun GetData(): MutableList<Status> =twitter.getFavorites(user.id, Paging(page))
}
