package xyz.donot.roselin.view.fragment.status


import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import twitter4j.Status
import twitter4j.Twitter
import xyz.donot.roselin.R
import xyz.donot.roselin.extend.SafeAsyncTask
import xyz.donot.roselin.util.extraUtils.newIntent
import xyz.donot.roselin.util.getMyId
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.view.activity.TwitterDetailActivity
import xyz.donot.roselin.view.adapter.StatusAdapter
import xyz.donot.roselin.view.fragment.BaseListFragment


abstract class TimeLineFragment : BaseListFragment<Status>() {
    var page: Int = 0
        set(value) {
            pagecopy=value
            field=page
        }
        get() {
            field++
            pagecopy=field
            return field
        }
   private var pagecopy: Int =0
    override fun adapterFun(): BaseQuickAdapter<Status, BaseViewHolder> =StatusAdapter()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.content_base_fragment, container, false)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //クリックリスナー
        adapter.setOnItemClickListener { adapter, _, position ->
            val status = adapter.data[position] as Status
            val item = if (status.isRetweet) {
                status.retweetedStatus
            } else {
                status
            }

            class DeleteTask : SafeAsyncTask<Twitter, Status>() {
                override fun doTask(arg: Twitter): twitter4j.Status = arg.destroyStatus(status.id)

                override fun onSuccess(result: twitter4j.Status) = Unit

                override fun onFailure(exception: Exception) = Unit
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
                                "削除" -> {
                                    DeleteTask().execute(getTwitterInstance())
                                }
                                "会話" -> {
                                    context.startActivity(context.newIntent<TwitterDetailActivity>(Bundle().apply { putSerializable("Status", item) }))

                                }
                                "コピー" -> {
                                    (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip = ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_URILIST, item.text)
                                }
                                "RTした人" -> {

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


                        })
                        .show()
            }
        }
        //クリックリスナーEnd
        if (savedInstanceState!=null)
      page=  savedInstanceState.getInt("page",0)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("page",pagecopy)
    }
}
