package xyz.donot.roselinx.ui.util.extraUtils
import android.app.DownloadManager
import android.app.NotificationManager
import android.app.job.JobScheduler
import android.content.Context
import android.net.ConnectivityManager
import android.view.inputmethod.InputMethodManager

fun Context.getConnectivityManager(): ConnectivityManager =
        getSystemServiceAs(Context.CONNECTIVITY_SERVICE)

fun Context.getDownloadManager(): DownloadManager =
        getSystemServiceAs(Context.DOWNLOAD_SERVICE)

fun Context.getInputMethodManager(): InputMethodManager =
        getSystemServiceAs(Context.INPUT_METHOD_SERVICE)

fun Context.getNotificationManager(): NotificationManager =
        getSystemServiceAs(Context.NOTIFICATION_SERVICE)

fun Context.getJobScheduler(): JobScheduler =
        getSystemServiceAs(Context.JOB_SCHEDULER_SERVICE)

@Suppress("UNCHECKED_CAST")
fun <T> Context.getSystemServiceAs(serviceName: String): T =
        this.getSystemService(serviceName) as T
