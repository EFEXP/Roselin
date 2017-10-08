package xyz.donot.roselinx.view.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Status
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.realm.CustomProfileObject
import xyz.donot.roselinx.util.extraUtils.*
import xyz.donot.roselinx.util.getAccount
import xyz.donot.roselinx.util.getDragdismiss
import xyz.donot.roselinx.view.activity.PictureActivity
import xyz.donot.roselinx.view.activity.TwitterDetailActivity
import xyz.donot.roselinx.view.activity.UserActivity
import xyz.donot.roselinx.view.activity.VideoActivity
import xyz.donot.roselinx.view.custom.TweetView

class StatusAdapter : BaseQuickAdapter<Status, BaseViewHolder>(R.layout.item_tweet_view) {

  //  private lateinit var kichitsui :List<Long>
    private lateinit var customname: RealmResults<CustomProfileObject>
    private lateinit var customnameId: List<Long>
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        launch(UI) {
          //  kichitsui=   async {  RoselinDatabase.getInstance(recyclerView.context).muteFilterDao().kichitsuiMuted().mapNotNull {it.id } }.await()
            customname = Realm.getDefaultInstance().where(CustomProfileObject::class.java).findAll()
            customnameId = customname.mapNotNull { it.id }
        }


    }

    override fun convert(helper: BaseViewHolder, status: Status) {
        helper.getView<TweetView>(R.id.tweetview).apply {
            var stringName: String? = null
            if (status.isRetweet) {
                if (customnameId.contains(status.retweetedStatus.user.id))
                    stringName = customname.first { it.id == status.retweetedStatus.user.id }.customname
                setStatus(status, status.retweetedStatus,
                   false
                        //kichitsui.contains(status.retweetedStatus.user.id)
                        , stringName)
            } else {
                if (customnameId.contains(status.user.id))
                    stringName = customname.first { it.id == status.user.id }.customname
                setStatus(status, status,false, stringName)
            }

            pictureClick = { position, images ->
                val i = mContext.getDragdismiss(PictureActivity.createIntent(mContext, images, position))
                (mContext as Activity).startActivity(i)
            }
            videoClick = { videoUrl, thumbUrl ->
                val i = mContext.newIntent<VideoActivity>(Bundle {
                    putString("video_url", videoUrl)
                    putString("thumbUrl", thumbUrl)
                })
                (mContext as Activity).startActivity(i)
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
                            val result = async(CommonPool) { getAccount().account.destroyFavorite(id) }.await()
                            setData(helper.adapterPosition - headerLayoutCount, result)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    launch(UI) {
                        try {
                            val result = async(CommonPool) {getAccount().account.createFavorite(id) }.await()
                            setData(helper.adapterPosition - headerLayoutCount, result)
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
                            val result = async(CommonPool) { getAccount().account.retweetStatus(id) }.await()
                            setData(helper.adapterPosition - headerLayoutCount, result)
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

