package xyz.donot.roselinx.view.fragment.status

import twitter4j.Paging
import twitter4j.Status
import twitter4j.User

class FavoriteTimeLine:TimeLineFragment(){
    val user by lazy {arguments.getSerializable("user") as User }
    override fun GetData(): MutableList<Status> =twitter.getFavorites(user.id, Paging(page))
}
