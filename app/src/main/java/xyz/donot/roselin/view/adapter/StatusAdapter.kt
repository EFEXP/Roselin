package xyz.donot.roselin.view.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import twitter4j.Status
import xyz.donot.roselin.R


class StatusAdapter(val context: Context,val list:List<Status>) : BaseQuickAdapter<Status, BaseViewHolder>(R.layout.item_tweet,list)
{


    override fun convert(helper: BaseViewHolder, item: Status) {
        helper.setText(R.id.textview_text,item.text)
    }


}

