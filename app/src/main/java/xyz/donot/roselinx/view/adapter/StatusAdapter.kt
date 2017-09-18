package xyz.donot.roselinx.view.adapter

import android.app.Activity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Status
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.*
import xyz.donot.roselinx.util.getTwitterInstance
import xyz.donot.roselinx.view.activity.PictureActivity
import xyz.donot.roselinx.view.activity.TwitterDetailActivity
import xyz.donot.roselinx.view.activity.UserActivity
import xyz.donot.roselinx.view.activity.VideoActivity
import xyz.donot.roselinx.view.custom.TweetView
import xyz.klinker.android.drag_dismiss.DragDismissIntentBuilder

class StatusAdapter : BaseQuickAdapter<Status, BaseViewHolder>(R.layout.item_tweet_view) {
    override fun convert(helper: BaseViewHolder, status: Status) {
        helper.getView<TweetView>(R.id.tweetview).apply {
            if (status.isRetweet) {
                setStatus(status, status.retweetedStatus)
            } else {
                setStatus(status, status)
            }
            pictureClick = { position, images ->
                val i = mContext.newIntent<PictureActivity>(
                        Bundle {
                            putInt("start_page", position)
                            putStringArrayList("picture_urls", images)
                        }
                )
                logd { images.size }
                DragDismissIntentBuilder(mContext)
                        .setShowToolbar(false)
                        .setDragElasticity(DragDismissIntentBuilder.DragElasticity.XXLARGE)
                        .build(i)
                (mContext as Activity).startActivity(i)
            }
            videoClick = { videoUrl, thumbUrl ->
                val i = mContext.newIntent<VideoActivity>(
                        Bundle {
                            putString("video_url", videoUrl)
                            putString("thumbUrl", thumbUrl)
                        }
                )
                DragDismissIntentBuilder(mContext)
                        .setShowToolbar(false)
                        .setDragElasticity(DragDismissIntentBuilder.DragElasticity.XXLARGE)
                        .build(i)
                mContext.startActivity(i)
            }
            userNameClick = { userName -> (mContext as Activity).startActivity(mContext.newIntent<UserActivity>(Bundle { putString("screen_name", userName.replace("@", "")) })) }
            quoteClick = {
                (mContext as Activity).start<TwitterDetailActivity>(Bundle { putSerializable("Status", it) })
            }
            iconClick = {
                val intent = mContext.intent<UserActivity>()
                intent.putExtra("user_id", it.id)
                mContext.startActivity(intent)
            }
            favoriteClick = { favorited, id ->
                if (favorited) {
                    launch(UI) {
                        try {
                            val result = async(CommonPool) { getTwitterInstance().destroyFavorite(id) }.await()
                            setData(helper.layoutPosition - 1, result)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    launch(UI) {
                        try {
                            val result = async(CommonPool) { getTwitterInstance().createFavorite(id) }.await()
                            setData(helper.layoutPosition - 1, result)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            retweetClick = { retweeted, id ->
                if (!retweeted) {
                    launch(UI) {
                        try {
                            val result = async(CommonPool) { getTwitterInstance().retweetStatus(id) }.await()
                            setData(helper.layoutPosition - 1, result)
                            mContext.toast("RTしました")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

        }
        //    val array= mContext.resources.getStringArray(R.array.ARRAY_KITITSUI)
        //      setText(R.id.textview_text,array[Random().nextInt(array.count())])
    }


}

