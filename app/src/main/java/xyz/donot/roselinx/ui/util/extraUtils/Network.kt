package xyz.donot.roselinx.util.extraUtils

import android.content.Context
import android.net.ConnectivityManager

enum class NetworkType {
    WIFI, MOBILE, OTHER, NONE
}

fun Context.networkTypeName(): String {
    var result = "(No Network)"
    try {
        val cm = this.getConnectivityManager()
        val info = cm.activeNetworkInfo
        if (info == null || !info.isConnectedOrConnecting) {
            return result
        }
        result = info.typeName
        if (info.type == ConnectivityManager.TYPE_MOBILE) {
            result += info.subtypeName
        }
    } catch (ignored: Throwable) {
    }
    return result
}


fun Context.networkType(): NetworkType {
    val cm = this.getConnectivityManager()
    val info = cm.activeNetworkInfo
    if (info == null || !info.isConnectedOrConnecting) {
        return NetworkType.NONE
    }
    val type = info.type
    return when (type) {
        ConnectivityManager.TYPE_WIFI -> NetworkType.WIFI
        ConnectivityManager.TYPE_MOBILE -> NetworkType.MOBILE
        else -> NetworkType.OTHER
    }
}

fun Context.isWifi(): Boolean = networkType() == NetworkType.WIFI

fun Context.isMobile(): Boolean = networkType() == NetworkType.MOBILE

fun Context.isConnected(): Boolean {
    val cm = this.getConnectivityManager()
    val info = cm.activeNetworkInfo
    return info != null && info.isConnectedOrConnecting
}
