package xyz.donot.roselin.view.fragment.status


import android.os.Bundle
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.android.synthetic.main.item_list.view.*
import twitter4j.ResponseList
import twitter4j.UserList
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.Bundle
import xyz.donot.roselin.view.activity.UserListsActivity
import xyz.donot.roselin.view.fragment.BaseListFragment


class UsersListFragment : BaseListFragment<UserList>() {
    private val userId by lazy { arguments.getLong("userId") }
    private val selectList by lazy { arguments.getBoolean("selectList",false) }

    override fun adapterFun(): BaseQuickAdapter<UserList, BaseViewHolder> =
        UserListAdapter()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.setOnItemClickListener { _, _, position ->
            val item=adapter.data[position]
            if(selectList){
                (activity as UserListsActivity).callbackMethod(item.id,item.name)

            }
            else{
               ListTimeLine().apply { arguments=Bundle{putLong("listId",item.id)} }.show(fragmentManager,"")
            }
        }
        adapter.setEnableLoadMore(false)
        refresh.isEnabled=false
    }

    override fun pullToRefresh(adapter: BaseQuickAdapter<UserList, BaseViewHolder>) {

    }

    override fun GetData(): ResponseList<UserList>?=  twitter.getUserLists(userId)





    inner class UserListAdapter:BaseQuickAdapter<UserList,BaseViewHolder>(R.layout.item_list)
    {
        override fun convert(helper: BaseViewHolder, item: UserList) {
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
