package xyz.donot.roselinx.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_user.view.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.customrecycler.CalculableRecyclerAdapter
import xyz.donot.roselinx.model.room.UserData
import xyz.donot.roselinx.util.extraUtils.inflate

class TwitterUserAdapter():CalculableRecyclerAdapter<TwitterUserAdapter.UserViewHolder,UserData>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder  = UserViewHolder(parent.context.inflate( R.layout.item_user,parent,false))

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item=itemList[position].user
        holder.itemView.apply {
            Picasso.with(context).load(item.biggerProfileImageURLHttps).into(iv_icon)
            tv_username.text=item.name
            tv_screenname.text= "@" + item.screenName
            tv_description.text= item.description

        }
    }

    override fun getItemId(position: Int): Long {
        if (position < itemList.size) {
            return itemList[position].id
        }
        return super.getItemId(position)
    }

    inner class UserViewHolder(view:View):RecyclerView.ViewHolder(view){


    }
}
