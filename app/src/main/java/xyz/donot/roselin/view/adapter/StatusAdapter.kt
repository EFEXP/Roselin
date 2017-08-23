package xyz.donot.roselin.view.adapter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.klinker.android.link_builder.LinkBuilder
import com.squareup.picasso.Picasso
import twitter4j.Status
import twitter4j.Twitter
import xyz.donot.roselin.R
import xyz.donot.roselin.extend.SafeAsyncTask
import xyz.donot.roselin.util.*
import xyz.donot.roselin.util.extraUtils.intent
import xyz.donot.roselin.util.extraUtils.start
import xyz.donot.roselin.view.activity.PictureActivity
import xyz.donot.roselin.view.activity.TweetEditActivity
import xyz.donot.roselin.view.activity.UserActivity
import xyz.donot.roselin.view.activity.VideoActivity
import java.util.*




class StatusAdapter : BaseQuickAdapter<Status, BaseViewHolder>(R.layout.item_tweet)
{
    override fun convert(helper: BaseViewHolder, status: Status) {

        val item= if (status.isRetweet){
            helper.setText(R.id.textview_is_retweet,"${status.user.name}がリツイート")
            helper.setVisible(R.id.textview_is_retweet,true)
            status.retweetedStatus }else{
            helper.setVisible(R.id.textview_is_retweet,false)
            status }
        //Define Task
        class RetweetTask : SafeAsyncTask<Twitter, Status>(){
            override fun doTask(arg: Twitter): twitter4j.Status = arg.retweetStatus(item.id)

            override fun onSuccess(result: twitter4j.Status) = replace(status,result)
            override fun onFailure(exception: Exception) = Unit
        }
        class FavoriteTask : SafeAsyncTask<Twitter, Status>(){
            override fun doTask(arg: Twitter): twitter4j.Status = arg.createFavorite(item.id)

            override fun onSuccess(result: twitter4j.Status) = replace(status,result)

            override fun onFailure(exception: Exception) = Unit
        }
        class DestroyFavoriteTask : SafeAsyncTask<Twitter, Status>(){
            override fun doTask(arg: Twitter): twitter4j.Status = arg.destroyFavorite(item.id)

            override fun onSuccess(result: twitter4j.Status) = replace(status,result)

            override fun onFailure(exception: Exception) = Unit
        }
        //Viewの初期化
        helper.apply {
            //キチツイ
            if(item.user.screenName==""){
                val array= mContext.resources.getStringArray(R.array.ARRAY_KITITSUI)
                setText(R.id.textview_text,array[Random().nextInt(array.count())])}
            else{ setText(R.id.textview_text, getExpandedText(item))}
            //ふぁぼ済み
            val fav= getView<TextView>(R.id.tv_favorite)
            if (item.isFavorited){
                fav.setCompoundDrawablesWithIntrinsicBounds( ResourcesCompat.getDrawable(mContext.resources, R.drawable.wrap_favorite_pressed,null),null,null, null)
            } else{
                fav.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(mContext.resources, R.drawable.wrap_favorite, null), null, null,null)
            }
            //RT
            val rt=getView<TextView>(R.id.tv_retweet)
            if (item.isRetweeted) { fav.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(mContext.resources, R.drawable.wrap_retweet_pressed ,null),null,null ,null) }
            else { rt.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(mContext.resources, R.drawable.wrap_retweet, null),null, null, null)
            }

            //認証済み
            if(item.user.isVerified || item.user.screenName=="JlowoIL"){
                getView<TextView>(R.id.textview_username)
                    .setCompoundDrawablesWithIntrinsicBounds(null,null, ResourcesCompat.getDrawable(mContext.resources, R.drawable.wraped_verify, null),null)}
            else{getView<TextView>(R.id.textview_username).setCompoundDrawablesWithIntrinsicBounds(null,null, null, null)}
            //鍵垢
            if(item.user.isProtected){
                getView<TextView>(R.id.textview_via)
                        .setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(mContext.resources, R.drawable.wrap_lock,null),null, null, null)}
            else{getView<TextView>(R.id.textview_via).setCompoundDrawablesWithIntrinsicBounds(null,null, null, null)}

            //テキスト関係
            setText(R.id.textview_username,item.user.name)
            setText(R.id.textview_screenname,"@"+item.user.screenName)
            setText(R.id.textview_via, getClientName(item.source))
            setText(R.id.textview_date, getRelativeTime(item.createdAt))
            setText(R.id.tv_retweet,item.retweetCount.toString())
            setText(R.id.tv_favorite,item.favoriteCount.toString())
           // setText(R.id.textview_count, "RT:${item.retweetCount} いいね:${item.favoriteCount}")



            LinkBuilder.on(getView(R.id.textview_text)).addLinks(mContext.getTagLinkList()).build()
            //Listener
            getView<ImageView>(R.id.imageview_icon).setOnClickListener{
                val intent=mContext.intent<UserActivity>()
                intent.putExtra("user_id",item.user.id)
                mContext.startActivity(intent)
            }
            getView<TextView>(R.id.tv_favorite).setOnClickListener{
                if(!item.isFavorited) {
                    FavoriteTask().execute(getTwitterInstance())
                }
                else{
                    DestroyFavoriteTask().execute(getTwitterInstance())
                }
                }
            getView<TextView>(R.id.reply).setOnClickListener{
                val bundle=  Bundle()
                bundle.putString("status_txt",item.text)
                bundle.putLong("status_id",item.id)
                bundle.putString("user_screen_name",item.user.screenName)
                (mContext as Activity).start<TweetEditActivity>(bundle)
            }
            getView<TextView>(R.id.tv_retweet).setOnClickListener{
                if(!item.isRetweeted){
                RetweetTask().execute(getTwitterInstance())}}
            }

        //mediaType
        val statusMediaIds=getImageUrls(item)
        if(statusMediaIds.isNotEmpty()){
            val mAdapter = TweetCardPicAdapter(statusMediaIds)
            val manager = LinearLayoutManager(mContext).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
             helper.getView<RecyclerView>(R.id.recyclerview_picture).apply {
                adapter=mAdapter
                layoutManager=manager
                visibility = View.VISIBLE
                hasFixedSize()
            }
            mAdapter.setOnItemClickListener { adapter, _, position ->
                val videoUrl: String? = getVideoURL(item.mediaEntities)
                if(videoUrl!=null){mContext.startActivity(Intent(mContext, VideoActivity::class.java).putExtra("video_url", videoUrl))}
                else{ ( mContext as Activity).start<PictureActivity>(Bundle().apply {
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

