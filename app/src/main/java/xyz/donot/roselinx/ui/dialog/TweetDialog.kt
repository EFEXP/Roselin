package xyz.donot.roselinx.ui.dialog

import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.Status
import xyz.donot.roselinx.R
import xyz.donot.roselinx.model.entity.TwitterAccount
import xyz.donot.roselinx.ui.detailtweet.TwitterDetailActivity
import xyz.donot.roselinx.ui.editteweet.EditTweetActivity
import xyz.donot.roselinx.ui.util.extraUtils.newIntent
import xyz.donot.roselinx.ui.util.extraUtils.start
import xyz.donot.roselinx.ui.util.extraUtils.toast

fun getTweetDialog(context: Activity, fragment: Fragment, myAccount: TwitterAccount, status: Status): AlertDialog.Builder? {
    val item = if (status.isRetweet) {
        status.retweetedStatus
    } else {
        status
    }
    if (!context.isFinishing) {
        val tweetItem = if (myAccount.id == status.user.id) {
            R.array.tweet_my_menu
        } else {
            R.array.tweet_menu
        }
        return AlertDialog.Builder(context)
                .setItems(tweetItem, { _, int ->
                    val selectedItem = context.resources.getStringArray(tweetItem)[int]
                    when (selectedItem) {
                        "返信" -> {
                            val bundle = Bundle()
                            bundle.putString("status_txt", item.text)
                            bundle.putLong("status_id", item.id)
                            bundle.putString("user_screen_name", item.user.screenName)
                            context.start<EditTweetActivity>(bundle)
                        }
                        "削除" -> {
                            launch(UI) {
                                try {
                                    async(CommonPool) { myAccount.account.destroyStatus(status.id) }.await()
                                    context.toast(context.getString(R.string.deleted_item))
                                } catch (e: Exception) {
                                    context.toast(e.localizedMessage)
                                }
                            }

                        }
                        "会話" -> {
                            context.startActivity(context.newIntent<TwitterDetailActivity>(Bundle().apply { putSerializable("Status", item) }))
                        }
                        "コピー" -> {
                            (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip = ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_URILIST, item.text)
                            context.toast("コピーしました")

                        }
                        "RTした人" -> {
                            RetweetUserDialog.getInstance(item.id).show(fragment.childFragmentManager, "")
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
    }
    return null
}
