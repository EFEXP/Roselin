package xyz.donot.roselinx.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import com.klinker.android.link_builder.Link
import xyz.donot.roselinx.R
import xyz.donot.roselinx.util.extraUtils.Bundle
import xyz.donot.roselinx.util.extraUtils.start
import xyz.donot.roselinx.view.activity.SearchActivity
import xyz.donot.roselinx.view.activity.UserActivity


fun Context.getTagURLMention() :MutableList<Link> = mutableListOf(
        Link(Regex.MENTION_PATTERN)
                .setUnderlined(false)
                .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setOnClickListener {
                    this.startActivity(Intent(this, UserActivity::class.java).putExtra("screen_name", it.replace("@",""))) }
        ,
        Link(Regex.VALID_URL)
                .setUnderlined(false)
                .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
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
                .setUnderlined(false)
                .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setOnClickListener {
                    (this as Activity).start<SearchActivity>(Bundle { putString("query_text","$it -rt") })
                }
)
fun Context.getURLLink() :MutableList<Link> = arrayListOf(
        Link(Regex.VALID_URL)
                .setUnderlined(false)
                .setTextColor(ContextCompat.getColor(this@getURLLink, R.color.colorPrimary))
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
                .setBold(true)
                .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setOnClickListener {
                    this.startActivity(Intent(this, UserActivity::class.java).putExtra("screen_name", it.replace("@","")))
                })

fun Context.getRetweetMention() :MutableList<Link> = arrayListOf(
        Link(Regex.MENTION_PATTERN)
                .setUnderlined(false)
                .setTextColor(ContextCompat.getColor(this, R.color.retweet))
                .setBold(true)
                .setOnClickListener {
                    this.startActivity(Intent(this, UserActivity::class.java).putExtra("screen_name", it.replace("@","")))
                })