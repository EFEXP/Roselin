package xyz.donot.roselinx.view.fragment.status


import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Status
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.*
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.view.activity.EditTweetActivity
import xyz.donot.roselinx.view.activity.TwitterDetailActivity
import xyz.donot.roselinx.view.adapter.StatusAdapter
import xyz.donot.roselinx.view.fragment.BaseListFragment
import xyz.donot.roselinx.view.fragment.RetweeterDialog


abstract class TimeLineFragment : BaseListFragment<Status>() {
    private var doubleClick=false
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

            viewmodel.adapter= StatusAdapter()
        super.onViewCreated(view, savedInstanceState)
        //クリックリスナー
        viewmodel.adapter.setOnItemClickListener { adapter, _, position ->
            if (doubleClick)
                return@setOnItemClickListener
            doubleClick=true
            Handler().delayed(500,{doubleClick=false})
            val status = adapter.data[position] as Status
            val item = if (status.isRetweet) {
                status.retweetedStatus
            } else {
                status
            }
            if (!(context as Activity).isFinishing) {
                val tweetItem = if (getMyId() == status.user.id) {
                    R.array.tweet_my_menu
                } else {
                    R.array.tweet_menu
                }
                AlertDialog.Builder(context)
                        .setItems(tweetItem, { _, int ->
                            val selectedItem = context.resources.getStringArray(tweetItem)[int]
                            when (selectedItem) {
                                "返信" -> {
                                    xyz.donot.roselinx.util.extraUtils.Bundle {
                                        putString("status_txt", item.text)
                                        putLong("status_id", item.id)
                                        putString("user_screen_name", item.user.screenName)
                                    }
                                    activity.start<EditTweetActivity>(
                                            xyz.donot.roselinx.util.extraUtils.Bundle {
                                                putString("status_txt", item.text)
                                                putLong("status_id", item.id)
                                                putString("user_screen_name", item.user.screenName)
                                            }
                                    )
                                }
                                "削除" -> {
                                    launch(UI) {
                                        try {
                                            async(CommonPool) { viewmodel.mainTwitter.destroyStatus(status.id) }.await()
                                            toast("削除しました")
                                        } catch (e: Exception) {
                                            toast(e.localizedMessage)
                                        }
                                    }

                                }
                                "会話" -> {
                                    context.startActivity(context.newIntent<TwitterDetailActivity>(Bundle().apply { putSerializable("Status", item) }))

                                }
                                "コピー" -> {
                                    (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip = ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_URILIST, item.text)
                                    toast("コピーしました")

                                }
                                "RTした人" -> {
                                    val rd = RetweeterDialog()
                                    rd.arguments = Bundle { putLong("tweetId", item.id) }
                                    rd.show(activity.supportFragmentManager, "")
                                }
                                "共有" -> {
                                    context.startActivity(Intent().apply {
                                        action = Intent.ACTION_SEND
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, "@${item.user.screenName}さんのツイート https://twitter.com/${item.user.screenName}/status/${item.id}をチェック")
                                    })
                                }
                                "公式で見る" -> {
                                    CustomTabsIntent.Builder()
                                            .setShowTitle(true)
                                            .addDefaultShareMenuItem()
                                            .setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
                                            .setStartAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                            .setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right).build()
                                            .launchUrl(context, Uri.parse("https://twitter.com/${item.user.screenName}/status/${item.id}"))
                                }
                            }
                        }).show()
            }
        }
        //クリックリスナーEnd
        viewmodel.adapter.emptyView = View.inflate(activity, R.layout.item_empty, null)
        refresh.isEnabled=true
    }



}
