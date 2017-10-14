package xyz.donot.roselinx.ui.util.extraUtils

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)


private fun inflateView(context: Context, layoutResId: Int, parent: ViewGroup?,
                        attachToRoot: Boolean): View =
        LayoutInflater.from(context).inflate(layoutResId, parent, attachToRoot)

fun Context.inflate(layoutResId: Int): View =
        inflateView(this, layoutResId, null, false)

fun Context.inflate(layoutResId: Int, parent: ViewGroup): View =
        inflate(layoutResId, parent, true)

fun Context.inflate(layoutResId: Int, parent: ViewGroup, attachToRoot: Boolean): View =
        inflateView(this, layoutResId, parent, attachToRoot)

fun Context.startPlayStoreLink(packageId: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageId"))
    if (intent.resolveActivity(packageManager) != null)
        startActivity(intent)
    else
        toast("Cannot resolve play store")
}

fun Context.mediaScan(uri: Uri) {
    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    intent.data = uri
    this.sendBroadcast(intent)
}

fun Context.getBatteryStatus(): Intent {
    val appContext = this.applicationContext
    return appContext.registerReceiver(null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED))
}

fun Context.getResourceValue(resId: Int): Int {
    val value = TypedValue()
    this.resources.getValue(resId, value, true)
    return TypedValue.complexToFloat(value.data).toInt()
}

inline fun Context.newNotification(func: NotificationCompat.Builder.() -> Unit,channel: String): Notification {
    val builder = NotificationCompat.Builder(this,channel)
    builder.func()
    return builder.build()
}
