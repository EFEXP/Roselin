package xyz.donot.roselin.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import com.klinker.android.link_builder.Link
import xyz.donot.roselin.R
import xyz.donot.roselin.util.extraUtils.Bundle
import xyz.donot.roselin.util.extraUtils.start
import xyz.donot.roselin.view.activity.SearchActivity
import xyz.donot.roselin.view.activity.UserActivity


fun Context.getTagURLMention() :MutableList<Link> = mutableListOf(
        Link(Regex.MENTION_PATTERN)
                .setUnderlined(false)
                .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setOnClickListener {
                    this.startActivity(Intent(this, UserActivity::class.java).putExtra("screen_name", it.replace("@",""))) }
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
                    (this as Activity).start<SearchActivity>(Bundle { putString("query_text",it) })
                }
)
fun Context.getURLLink() :MutableList<Link> = arrayListOf(
        Link(Regex.VALID_URL)
                .setUnderlined(false)
                .setTextColor(ContextCompat.getColor(this@getURLLink, R.color.colorAccent))
                .setOnClickListener {
                    CustomTabsIntent.Builder()
                            .setShowTitle(true)
                            .addDefaultShareMenuItem()
                            .setToolbarColor(ContextCompat.getColor(this@getURLLink, R.color.colorPrimary))
                            .setStartAnimations(this@getURLLink, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            .setExitAnimations(this@getURLLink, android.R.anim.slide_in_left, android.R.anim.slide_out_right).build()
                            .launchUrl(this@getURLLink, Uri.parse(it))})

fun Context.getMentionLink() :MutableList<Link> = arrayListOf(
        Link(Regex.MENTION_PATTERN)
                .setUnderlined(false)
                .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setOnClickListener {
                    this.startActivity(Intent(this, UserActivity::class.java).putExtra("screen_name", it.replace("@","")))
                }
)