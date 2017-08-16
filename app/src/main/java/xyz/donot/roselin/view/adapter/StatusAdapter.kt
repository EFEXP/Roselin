package xyz.donot.roselin.view.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.LinkBuilder
import com.squareup.picasso.Picasso
import twitter4j.Status
import xyz.donot.roselin.R
import xyz.donot.roselin.util.*
import xyz.donot.roselin.util.extraUtils.intent
import xyz.donot.roselin.util.extraUtils.start
import xyz.donot.roselin.view.activity.PictureActivity
import xyz.donot.roselin.view.activity.UserActivity
import xyz.donot.roselin.view.activity.VideoActivity
import java.util.*




class StatusAdapter(val context: Context,list:List<Status>) : BaseQuickAdapter<Status, BaseViewHolder>(R.layout.item_tweet,list)
{
    override fun convert(helper: BaseViewHolder, status: Status) {
        val item= if (status.isRetweet){
            helper.setText(R.id.textview_is_retweet,"${status.user.name}がリツイート")
            helper.setVisible(R.id.textview_is_retweet,true)
            status.retweetedStatus }else{
            helper.setVisible(R.id.textview_is_retweet,false)
            status }
        //Viewの初期化
        helper.apply {
            //キチツイ
            if(item.user.screenName==""){
                val array= context.resources.getStringArray(R.array.ARRAY_KITITSUI)
                setText(R.id.textview_text,array[Random().nextInt(array.count())])}
            else{ setText(R.id.textview_text, getExpandedText(item))}
            //認証済み
            if(item.user.isVerified){
                getView<TextView>(R.id.textview_username)
                    .setCompoundDrawablesWithIntrinsicBounds(null,null, ResourcesCompat.getDrawable(context.resources, R.drawable.ic_check_circle_black_18dp, null),null)}
            else{getView<TextView>(R.id.textview_username).setCompoundDrawablesWithIntrinsicBounds(null,null, null, null)}
            //テキスト関係
            setText(R.id.textview_username,item.user.name)
            setText(R.id.textview_screenname,"@"+item.user.screenName)
            setText(R.id.textview_via, getClientName(item.source))
            setText(R.id.textview_date, getRelativeTime(item.createdAt))
            setText(R.id.textview_count, "RT:${item.retweetCount} いいね:${item.favoriteCount}")
            LinkBuilder.on(getView(R.id.textview_text)).addLinks(getLinkList(context)).build()
            //Listener
            getView<ImageView>(R.id.imageview_icon).setOnClickListener{
                val intent=context.intent<UserActivity>()
                intent.putExtra("user_id",item.user.id)
               context.startActivity(intent)
            }



        }
        //mediaType
        val statusMediaIds=getImageUrls(item)
        if(statusMediaIds.isNotEmpty()){
            val mAdapter = TweetCardPicAdapter(statusMediaIds)
            val manager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
             helper.getView<RecyclerView>(R.id.recyclerview_picture).apply {
                adapter=mAdapter
                layoutManager=manager
                visibility = View.VISIBLE
                hasFixedSize()
            }
            mAdapter.setOnItemClickListener { adapter, _, position ->
                val videoUrl: String? = getVideoURL(status.mediaEntities)
                if(videoUrl!=null){context.startActivity(Intent(context, VideoActivity::class.java).putExtra("video_url", videoUrl))}
                else{ ( context as Activity).start<PictureActivity>(Bundle().apply {
                    putStringArrayList("picture_urls",getImageUrls(item))
                })}
                }

        }
        else{
            helper.getView<RecyclerView>(R.id.recyclerview_picture).visibility = View.GONE
        }
        //EndMedia
        Picasso.with(mContext).load(item.user.originalProfileImageURLHttps).into(helper.getView<ImageView>(R.id.imageview_icon))




    }
}
fun getLinkList(context: Context) :MutableList<Link> {
    return   listOf<Link>(
            Link(xyz.donot.roselin.util.Regex.MENTION_PATTERN)
                    .setUnderlined(false)
                    .setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                    .setOnClickListener {
                       context.startActivity(Intent(context, UserActivity::class.java).putExtra("screen_name", it.replace("@","")))
                    }
            ,
            Link(xyz.donot.roselin.util.Regex.VALID_URL)
                    .setUnderlined(false)
                    .setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                    .setOnClickListener {
                        val builder = CustomTabsIntent.Builder()
                        val customTabsIntent = builder.build()
                        customTabsIntent.launchUrl(context, Uri.parse(it))
                    }
            ,
            Link(xyz.donot.roselin.util.Regex.HASHTAG_PATTERN)
                    .setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                    .setOnClickListener {

                    }
    ).toMutableList()
}

