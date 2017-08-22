package xyz.donot.roselin.view.fragment


import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.Status
import twitter4j.Twitter
import xyz.donot.roselin.R
import xyz.donot.roselin.extend.SafeAsyncTask
import xyz.donot.roselin.util.extraUtils.newIntent
import xyz.donot.roselin.util.getMyId
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.view.activity.TwitterDetailActivity
import xyz.donot.roselin.view.adapter.StatusAdapter
import xyz.donot.roselin.view.custom.MyLoadingView



abstract class TimeLineFragment : Fragment() {
    val twitter by lazy { getTwitterInstance() }
    val adapter by lazy { StatusAdapter() }
    abstract fun loadMore(adapter:BaseQuickAdapter<Status,BaseViewHolder>)
    abstract fun   pullToRefresh(adapter:BaseQuickAdapter<Status,BaseViewHolder>)
    var page: Int = 0
        get() {
            field++
            return field
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.content_base_fragment, container, false)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dividerItemDecoration = DividerItemDecoration( recycler.context,
                LinearLayoutManager(activity).orientation)
        recycler.addItemDecoration(dividerItemDecoration)
        recycler.layoutManager = LinearLayoutManager(activity)
       // adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
        adapter.setOnLoadMoreListener({  loadMore(adapter) },recycler)
        adapter.setLoadMoreView(MyLoadingView())
       // adapter.emptyView=View.inflate(activity, R.layout.item_empty,null)
        recycler.adapter=adapter
        loadMore(adapter)
        refresh.setOnRefreshListener {
            if (adapter.data.isNotEmpty()){
                pullToRefresh(adapter)

                refresh.isRefreshing=false
            }
            else{
                loadMore(adapter)
                refresh.isRefreshing=false
            } }
        //

        //クリックリスナー
        adapter.setOnItemClickListener { adapter, _, position ->
            val status=adapter.data[position] as Status
            val item=  if (status.isRetweet){ status.retweetedStatus }else{ status }
            class DeleteTask: SafeAsyncTask<Twitter, Status>(){
                override fun doTask(arg: Twitter): twitter4j.Status = arg.destroyStatus(status.id)

                override fun onSuccess(result: twitter4j.Status) = Unit

                override fun onFailure(exception: Exception) = Unit
            }
            if( !(context as Activity).isFinishing){
                val tweetItem=if(getMyId() ==status.user.id){ R.array.tweet_my_menu}else{R.array.tweet_menu}
                AlertDialog.Builder(context)
                        .setItems(tweetItem, { _, int ->
                            val selectedItem=context.resources.getStringArray(tweetItem)[int]
                            when (selectedItem) {
                                "削除" -> {
                                    DeleteTask().execute(getTwitterInstance())
                                }
                                "会話" -> {
                                    context. startActivity(context.newIntent<TwitterDetailActivity>(Bundle().apply {putSerializable("Status",item) }))

                                }
                                "コピー" -> {
                                    (context.getSystemService(Context.CLIPBOARD_SERVICE)as ClipboardManager).primaryClip = ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_URILIST,item.text)
                                }
                                "RTした人"-> {

                                }
                                "共有"-> {
                                    context. startActivity( Intent().apply {
                                        action = Intent.ACTION_SEND
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT,"@${item.user.screenName}さんのツイート https://twitter.com/${item.user.screenName}/status/${item.id}をチェック")
                                    })
                                }
                                "公式で見る"-> {
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
    }


}
