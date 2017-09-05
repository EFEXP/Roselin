package xyz.donot.roselinx.view.fragment.status

import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_base_fragment.*
import kotlinx.android.synthetic.main.content_base_fragment.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Query
import twitter4j.Status
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.*
import xyz.donot.roselinx.util.getMyId
import xyz.donot.roselinx.util.getTwitterInstance
import xyz.donot.roselinx.view.activity.TweetEditActivity
import xyz.donot.roselinx.view.activity.TwitterDetailActivity
import xyz.donot.roselinx.view.adapter.StatusAdapter
import xyz.donot.roselinx.view.fragment.RetweeterDialog


class ConversationFragment : Fragment(){
    val status by lazy {arguments.getSerializable("status") as Status }
    val adapter by lazy { StatusAdapter() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadReply(status.id)
        getDiscuss(status)
        refresh.isEnabled=false
        view.recycler.adapter = adapter
        view.recycler.layoutManager = LinearLayoutManager(activity)
        //クリックリスナー
        adapter.setOnItemClickListener { adapter, _, position ->
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
                                "返信"->{
                                    val bundle=  Bundle()
                                    bundle.putString("status_txt",item.text)
                                    bundle.putLong("status_id",item.id)
                                    bundle.putString("user_screen_name",item.user.screenName)
                                    activity.start<TweetEditActivity>(bundle)
                                }
                                "削除" -> {
                                    launch(UI){
                                        try {
                                            async(CommonPool){ getTwitterInstance().destroyStatus(status.id)}.await()
                                            toast("削除しました")
                                        }catch (e:Exception){
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
                                    val rd= RetweeterDialog()
                                    rd.arguments= Bundle{putLong("tweetId",item.id)}
                                    rd.show(activity.supportFragmentManager,"")
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
        adapter.emptyView=View.inflate(activity, R.layout.item_empty,null)



    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.content_base_fragment, container, false)


    fun loadReply(long: Long){
        launch(UI){
            try {
                val result= async(CommonPool){ getTwitterInstance().showStatus(long)}.await()
                adapter.addData(0,result)
                val voo=result.inReplyToStatusId>0
                if(voo){
                    loadReply(result.inReplyToStatusId)
                }
            } catch (e: Exception) {
                activity. twitterExceptionToast(e)
            }
        }
    }


    private fun getDiscuss(status: Status){
        val twitter by lazy { getTwitterInstance() }
        val screenname = status.user.screenName
        val query= Query("@$screenname since_id:${status.id}")
        query.count=100
        context.logd {  query.count.toString()}
        launch(UI){
            try {
                val result= async(CommonPool){ twitter.search(query)}.await()
                for (tweet in result.tweets){
                    if (tweet.inReplyToStatusId == status.id){adapter.addData(tweet)}
                }

            } catch (e: Exception) {
               activity. twitterExceptionToast(e)
            }
        }
}}
