package xyz.donot.roselinx.ui.status

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_tweet_view.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.donot.roselinx.R
import xyz.donot.roselinx.customrecycler.CalculableAdapter
import xyz.donot.roselinx.model.entity.CustomProfile
import xyz.donot.roselinx.model.entity.RoselinDatabase
import xyz.donot.roselinx.model.entity.Tweet
import xyz.donot.roselinx.ui.detailtweet.TwitterDetailActivity
import xyz.donot.roselinx.ui.detailuser.UserActivity
import xyz.donot.roselinx.ui.picture.PictureActivity
import xyz.donot.roselinx.ui.util.extraUtils.*
import xyz.donot.roselinx.ui.util.getAccount
import xyz.donot.roselinx.ui.util.getDragdismiss
import xyz.donot.roselinx.ui.video.VideoActivity

class KViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

class TweetAdapter(private val mContext: Context) : CalculableAdapter<Tweet>(R.layout.item_tweet_view) {


    //  private lateinit var kichitsui :List<Long>
    private lateinit var customname: List<CustomProfile>
    private lateinit var customnameId: List<Long>
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        launch(UI) {
            //  kichitsui=   async {  RoselinDatabase.getInstance(recyclerView.context).muteFilterDao().kichitsuiMuted().mapNotNull {it.tweetId } }.await()
            customname = async { RoselinDatabase.getInstance().customProfileDao().getAllData() }.await()
            customnameId = customname.map { it.userId }
        }
    }

    override fun onBindViewHolder(holder: KViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val status = getItem(position)!!.status
        holder.containerView.
                tweetview.apply {
            var stringName: String? = null
            if (status.isRetweet) {
                if (customnameId.contains(status.retweetedStatus.user.id))
                    stringName = customname.first { it.userId == status.retweetedStatus.user.id }.customname
                setStatus(status, status.retweetedStatus,
                        false
                        //kichitsui.contains(status.retweetedStatus.user.tweetId)
                        , stringName)
            } else {
                if (customnameId.contains(status.user.id))
                    stringName = customname.first { it.userId == status.user.id }.customname
                setStatus(status, status, false, stringName)
            }

            pictureClick = { position, images ->
                val i = mContext.getDragdismiss(PictureActivity.createIntent(mContext, images, position))
                (mContext as Activity).startActivity(i)
            }
            videoClick = { videoUrl, thumbUrl ->
                val i = mContext.newIntent<VideoActivity>(bundle {
                    putString("video_url", videoUrl)
                    putString("thumbUrl", thumbUrl)
                })
                (mContext as Activity).startActivity(i)
            }
            userNameClick = { userName -> (mContext as Activity).startActivity(mContext.newIntent<UserActivity>(bundle { putString("screen_name", userName.replace("@", "")) })) }
            quoteClick = {
                (mContext as Activity).start<TwitterDetailActivity>(bundle { putSerializable("Status", it) })
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
                            val result = async{ getAccount().account.destroyFavorite(id) }.await()
                            Tweet.update(result)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    launch(UI) {
                        try {
                            val result = async{ getAccount().account.createFavorite(id) }.await()
                            Tweet.update(result)
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
                            val result = async { getAccount().account.retweetStatus(id) }.await()
                            Tweet.update(result)
                            mContext.toast("RTしました")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

        }
    }
}
