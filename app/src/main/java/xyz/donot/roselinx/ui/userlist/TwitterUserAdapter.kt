package xyz.donot.roselinx.ui.userlist

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_user.view.*
import xyz.donot.roselinx.R
import xyz.donot.roselinx.customrecycler.CalculableAdapter
import xyz.donot.roselinx.customrecycler.CalculableRecyclerAdapter
import xyz.donot.roselinx.model.entity.TwitterAccount
import xyz.donot.roselinx.model.entity.UserData
import xyz.donot.roselinx.ui.status.KViewHolder

class TwitterUserAdapter: CalculableAdapter<UserData>(R.layout.item_user){
    override fun onBindViewHolder(holder: KViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item=getItem(position)?.user!!
        holder.containerView.apply {
            Picasso.with(context).load(item.biggerProfileImageURLHttps).into(iv_icon)
            tv_username.text=item.name
            tv_screenname.text= "@" + item.screenName
            tv_description.text= item.description

        }
    }
}

class TwitterAccountAdapter:CalculableAdapter<TwitterAccount>(R.layout.item_user){
    override fun onBindViewHolder(holder: KViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item=getItem(position)?.user!!
        holder.containerView.apply {
            Picasso.with(context).load(item.biggerProfileImageURLHttps).into(iv_icon)
            tv_username.text=item.name
            tv_screenname.text= "@" + item.screenName
            tv_description.text= item.description

        }
    }

}

class TwitterUserPreAdapter: CalculableRecyclerAdapter<UserData>(R.layout.item_user){


    override fun onBindViewHolder(holder: KViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item=itemList[position].user
        holder.containerView.apply {
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
}
