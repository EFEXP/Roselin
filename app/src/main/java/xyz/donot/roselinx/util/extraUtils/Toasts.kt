package xyz.donot.roselinx.util.extraUtils

import android.content.Context
import android.support.v4.app.Fragment
import android.widget.Toast
import twitter4j.TwitterException

fun Context.toast(messageResId: Int) = mainThread { Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show() }

fun Context.twitterExceptionToast(exception: Exception) = mainThread {
    if (exception is TwitterException) {
        val text = when (exception.errorCode) {
            32 -> {
                "ユーザーを認証できませんでした"
            }
            34 -> {
                "ページが見つかりませんでした"
            }
            64 -> {
                "あなたのアカウントは凍結されています"
            }
            88 -> {
                "レート制限を超えました。"
            }
            130 -> {
                "Twitterがダウンしています"
            }
            131 -> {
                "内部エラー"
            }
            135 -> {
                "ユーザーを認証できませんでした。"
            }
            161 -> {
                "今はこれ以上フォローできません。"
            }
            179 -> {
                "このステータスを見る権限がありません"
            }
            185 -> {
                "一日のTweet回数制限をオーバーしました。"
            }
            187 -> {
                "ステータスが重複しています。"
            }
            226 -> {
                "このリクエストは自動送信の疑いがあります。悪意ある行動から他のユーザを守るため、このアクションは実行されませんでした。"
            }
            261 -> {
                "アプリケーションに書き込み権限がありません。"
            }
            else -> {
                throw exception
            }
        }

        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
    else    Toast.makeText(this,exception.localizedMessage, Toast.LENGTH_SHORT).show()
 }

fun Context.longToast(messageResId: Int) = mainThread {  Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show()}

fun Context.toast(message: String) = mainThread { Toast.makeText(this, message, Toast.LENGTH_SHORT).show()}

fun Context.longToast(message: String) = mainThread { Toast.makeText(this, message, Toast.LENGTH_LONG).show()}

fun Fragment.toast(messageResId: Int) = mainThread {  Toast.makeText(activity, messageResId, Toast.LENGTH_SHORT).show()}

fun Fragment.longToast(messageResId: Int) = Toast.makeText(activity, messageResId, Toast.LENGTH_LONG).show()

fun Fragment.toast(message: String) = mainThread {  Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()}

fun Fragment.longToast(message: String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()

