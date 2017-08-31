package xyz.donot.roselin.view.adapter

import android.app.Activity
import android.content.Intent
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.klinker.android.link_builder.LinkBuilder
import com.squareup.picasso.Picasso
import io.realm.Realm
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Status
import xyz.donot.roselin.R
import xyz.donot.roselin.model.realm.DBChangeName
import xyz.donot.roselin.util.*
import xyz.donot.roselin.util.extraUtils.*
import xyz.donot.roselin.view.activity.PictureActivity
import xyz.donot.roselin.view.activity.TwitterDetailActivity
import xyz.donot.roselin.view.activity.UserActivity
import xyz.donot.roselin.view.activity.VideoActivity
import xyz.donot.roselin.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselin.view.custom.MyViewHolder






class StatusAdapter : MyBaseRecyclerAdapter<Status, MyViewHolder>(R.layout.item_classic_tweet)
{
    override fun convert(helper: MyViewHolder, status: Status) {
        val item= if (status.isRetweet){
            helper.setText(R.id.textview_is_retweet,"@${status.user.screenName}がリツイート")
            LinkBuilder.on( helper.getView(R.id.textview_is_retweet)).addLinks(mContext.getMentionLink()).build()
            helper.setVisible(R.id.textview_is_retweet,true)
            status.retweetedStatus }
        else{
            helper.setVisible(R.id.textview_is_retweet,false)
            status }
        //Viewの初期化
        helper.apply {
            //テキスト関係
            launch(UI){
                val relative=  async(CommonPool){getRelativeTime(item.createdAt)}.await()
                val text= async(CommonPool){ getExpandedText(item)}.await()
                setText(R.id.textview_date,relative)
                setText(R.id.textview_text,text)
                LinkBuilder.on(getView(R.id.textview_text)).addLinks(mContext.getTagURLMention()).build()
            }
            Realm.getDefaultInstance().where(DBChangeName::class.java).equalTo("id",item.user.id).findFirst()?.let {
                setText(R.id.textview_username,it.name)
            }?:setText(R.id.textview_username,item.user.name)
            setText(R.id.textview_screenname,"@"+item.user.screenName)
            setText(R.id.textview_via, getClientName(item.source))
            setText(R.id.tv_retweet,item.retweetCount.toString())
            setText(R.id.tv_favorite,item.favoriteCount.toString())
            //    val array= mContext.resources.getStringArray(R.array.ARRAY_KITITSUI)
            //      setText(R.id.textview_text,array[Random().nextInt(array.count())])
            //ふぁぼ済み
            val fav= getView<TextView>(R.id.tv_favorite)
            if (item.isFavorited){
                fav.setCompoundDrawablesWithIntrinsicBounds( ResourcesCompat.getDrawable(mContext.resources, R.drawable.wrap_favorite_pressed,null),null,null, null)
            } else{
                fav.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(mContext.resources, R.drawable.wrap_favorite, null), null, null,null)
            }
            //RT
            val rt=getView<TextView>(R.id.tv_retweet)
            if (item.isRetweeted) { rt.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(mContext.resources, R.drawable.wrap_retweet_pressed ,null),null,null ,null) }
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
            //引用
            item.quotedStatus?.let {
                setVisible(R.id.quote_tweet_holder,true)
                setText(R.id.quoted_screenname, "@"+ item.quotedStatus.user.screenName)
                setText(R.id.quoted_text,  item.quotedStatus.text)
                setText(R.id.quoted_name,item.quotedStatus.user.name)
                Picasso.with(mContext).load(item.quotedStatus.user.biggerProfileImageURLHttps).resize(100,100).into(  getView<ImageView>(R.id.quoted_icon))
            }?:  getView<View>(R.id.quote_tweet_holder).hide()
            //Listener
            getView<View>(R.id.quote_tweet_holder).onClick {
                ( mContext as Activity).start<TwitterDetailActivity>(Bundle { putSerializable("Status",item.quotedStatus) })
            }
            getView<ImageView>(R.id.imageview_icon).setOnClickListener{
                val intent=mContext.intent<UserActivity>()
                intent.putExtra("user_id",item.user.id)
                mContext.startActivity(intent)
            }
            getView<TextView>(R.id.tv_favorite).setOnClickListener{
                if(item.isFavorited) {
                    launch(UI){
                        try {
                            val result= async(CommonPool){getTwitterInstance().destroyFavorite(status.id)}.await()
                            replace(status,result)
                        } catch (e: Exception) {
                            mContext.tExceptionToast(e)
                        }
                    }
                }
                else{
                    launch(UI){
                        try {
                            val result= async(CommonPool){getTwitterInstance().createFavorite(status.id)}.await()
                            replace(status,result)
                        } catch (e: Exception) {
                            mContext.tExceptionToast(e)
                        }
                    }
                }
                }
            getView<TextView>(R.id.tv_retweet).setOnClickListener{
                if(!status.isRetweeted){
                    launch(UI){
                        try {
                            val result= async(CommonPool){getTwitterInstance().retweetStatus(status.id)}.await()
                            replace(status,result)
                           mContext. toast("RTしました")
                        } catch (e: Exception) {
                            mContext.tExceptionToast(e)
                        }
                    }
                }


                  }
            }

        //mediaType
        val statusMediaIds=item.images
        if(statusMediaIds.isNotEmpty()){
            val mAdapter =TweetCardPicAdapter(statusMediaIds)
            val manager = LinearLayoutManager(mContext).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            helper.getView<RecyclerView>(R.id.recyclerview_picture).apply {
                adapter=mAdapter
                layoutManager=manager
                visibility = View.VISIBLE
                hasFixedSize()
            }
            mAdapter.setOnItemClickListener {  _, _,  _ ->
                if(item.hasVideo){mContext.startActivity(Intent(mContext,VideoActivity::class.java).putExtra("video_url", item.getVideoURL()))}
                else{( mContext as Activity).start<PictureActivity>(Bundle { putStringArrayList("picture_urls",item.images) })}
                }
        }
        else{
            helper.getView<RecyclerView>(R.id.recyclerview_picture).visibility = View.GONE
        }
        //EndMedia
        Picasso.with(mContext).load(item.user.biggerProfileImageURLHttps).into(helper.getView<ImageView>(R.id.imageview_icon))
    }
}

