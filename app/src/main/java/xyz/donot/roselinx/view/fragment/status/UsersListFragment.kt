package xyz.donot.roselinx.view.fragment.status


import android.os.Bundle
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.android.synthetic.main.item_list.view.*
import twitter4j.ResponseList
import twitter4j.UserList
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.Bundle
import xyz.donot.roselinx.view.activity.UserListsActivity
import xyz.donot.roselinx.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselinx.view.custom.MyViewHolder
import xyz.donot.roselinx.view.fragment.BaseListFragment


class UsersListFragment : BaseListFragment<UserList>() {
    private val userId by lazy { arguments.getLong("userId") }
    private val selectList by lazy { arguments.getBoolean("selectList",false) }

    override fun adapterFun(): MyBaseRecyclerAdapter<UserList,MyViewHolder> =
        UserListAdapter()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.setEnableLoadMore(false)
        adapter.setOnItemClickListener { _, _, position ->
            val item=adapter.data[position]
            if(selectList){
                (activity as UserListsActivity).callbackMethod(item.id,item.name)
            }
            else{
               ListTimeLine().apply { arguments=Bundle{putLong("listId",item.id)} }.show(fragmentManager,"")
            }
        }
        refresh.isEnabled=false
    }

    override fun GetData(): ResponseList<UserList>?{
        shouldLoad=false
        adapter.loadMoreComplete()
      return  twitter.getUserLists(userId)
    }
    inner class UserListAdapter:MyBaseRecyclerAdapter<UserList,MyViewHolder>(R.layout.item_list)
    {
        override fun convert(helper:MyViewHolder, item: UserList,position:Int) {
            helper.getView<View>(R.id.item_list_root).apply {
                tv_author.text=item.user.name
                Picasso.with(context).load(item.user.biggerProfileImageURLHttps).into(iv_icon)
                tv_description.text=item.description
                tv_list_name.text=item.name
                tv_listed_user.text="${item.memberCount}人のユーザー"
            }

        }
    }
}
