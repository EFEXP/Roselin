package xyz.donot.roselin.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import com.klinker.android.link_builder.Link
import xyz.donot.roselin.R
import xyz.donot.roselin.view.activity.UserActivity

fun Context.getTagLinkList() :MutableList<Link> = arrayListOf(
        Link(Regex.MENTION_PATTERN)
                .setUnderlined(false)
                .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setOnClickListener {
                    this.startActivity(Intent(this, UserActivity::class.java).putExtra("screen_name", it.replace("@","")))
                }
        ,
        Link(Regex.VALID_URL)
                .setUnderlined(false)
                .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setOnClickListener {

                    CustomTabsIntent.Builder()
                            .setShowTitle(true)
                            .addDefaultShareMenuItem()
                            .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                            .setStartAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            .setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right).build()
                            .launchUrl(this, Uri.parse(it))
                }
        ,
        Link(Regex.HASHTAG_PATTERN)
                .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setOnClickListener {

                }
)
fun Context.getLinkList() :MutableList<Link> = arrayListOf(
        Link(Regex.VALID_URL)
                .setUnderlined(false)
                .setTextColor(ContextCompat.getColor(this@getLinkList, R.color.colorAccent))
                .setOnClickListener {
                    CustomTabsIntent.Builder()
                            .setShowTitle(true)
                            .addDefaultShareMenuItem()
                            .setToolbarColor(ContextCompat.getColor(this@getLinkList, R.color.colorPrimary))
                            .setStartAnimations(this@getLinkList, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            .setExitAnimations(this@getLinkList, android.R.anim.slide_in_left, android.R.anim.slide_out_right).build()
                            .launchUrl(this@getLinkList, Uri.parse(it))
                }
)

