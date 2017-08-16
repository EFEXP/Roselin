package xyz.donot.roselin.view.fragment


import android.app.Activity
import android.content.*
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.content_base_fragment.*
import twitter4j.Status
import twitter4j.Twitter
import xyz.donot.quetzal.view.fragment.getMyId
import xyz.donot.roselin.R
import xyz.donot.roselin.extend.SafeAsyncTask
import xyz.donot.roselin.util.extraUtils.longToast
import xyz.donot.roselin.util.getTwitterInstance
import xyz.donot.roselin.view.adapter.StatusAdapter
import xyz.donot.roselin.view.custom.MyLoadingView

abstract class TimeLineFragment : AppCompatDialogFragment() {
    val twitter by lazy { getTwitterInstance() }
    val adapter by lazy { StatusAdapter(activity, mutableListOf()) }
    abstract fun loadMore(adapter:BaseQuickAdapter<Status,BaseViewHolder>)
    abstract fun   pullToRefresh(adapter:BaseQuickAdapter<Status,BaseViewHolder>)
    var page: Int = 0
        get() {
            field++
            return field
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.content_base_fragment, container, false)
    }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler.layoutManager = LinearLayoutManager(activity)
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
        adapter.setOnLoadMoreListener({  loadMore(adapter) },recycler)
        adapter.setLoadMoreView(MyLoadingView())
        adapter.emptyView=View.inflate(activity, R.layout.item_empty,null)
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
        adapter.setOnItemClickListener { adapter, view, position ->
            val status=adapter.data[position] as Status
            val item=  if (status.isRetweet){ status.retweetedStatus }else{ status }
            class DeleteTask: SafeAsyncTask<Twitter, Status>(){
                override fun doTask(arg: Twitter): twitter4j.Status {
                    return arg.destroyStatus(status.id)
                }

                override fun onSuccess(result: twitter4j.Status) {
                    context.longToast("削除しました")
                }

                override fun onFailure(exception: Exception) {

                }
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

                                }
                            }


                        })
                        .show()
            }
        }
    }
}
