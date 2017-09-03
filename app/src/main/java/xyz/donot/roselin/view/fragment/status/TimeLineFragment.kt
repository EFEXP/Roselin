package xyz.donot.roselin.view.fragment.status


import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Status
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.Bundle
import xyz.donot.roselin.util.extraUtils.newIntent
import xyz.donot.roselin.util.extraUtils.start
import xyz.donot.roselin.util.extraUtils.toast
import xyz.donot.roselin.util.getMyId
import xyz.donot.roselin.view.activity.TweetEditActivity
import xyz.donot.roselin.view.activity.TwitterDetailActivity
import xyz.donot.roselin.view.adapter.StatusAdapter
import xyz.donot.roselin.view.custom.MyBaseRecyclerAdapter
import xyz.donot.roselin.view.custom.MyViewHolder
import xyz.donot.roselin.view.fragment.BaseListFragment
import xyz.donot.roselin.view.fragment.RetweeterDialog


abstract class TimeLineFragment : BaseListFragment<Status>() {
	var page: Int = 0
		set(value) {
			pagecopy = value
			field = value
		}
		get() {
			field++
			pagecopy = field
			return field
		}
	private var pagecopy: Int = 0
	override fun adapterFun(): MyBaseRecyclerAdapter<Status, MyViewHolder> = StatusAdapter()
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
									val bundle = Bundle()
									bundle.putString("status_txt", item.text)
									bundle.putLong("status_id", item.id)
									bundle.putString("user_screen_name", item.user.screenName)
									activity.start<TweetEditActivity>(bundle)
								}
								"削除" -> {
									launch(UI) {
										try {
											async(CommonPool) { main_twitter.destroyStatus(status.id) }.await()
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
		adapter.emptyView = View.inflate(activity, R.layout.item_empty, null)
		if (savedInstanceState != null)
			page = savedInstanceState.getInt("page", 0)
	}


	override fun onSaveInstanceState(outState: Bundle?) {
		super.onSaveInstanceState(outState)
		outState?.putInt("page", pagecopy)
	}
}
