package xyz.donot.roselinx.ui.util.extraUtils
import android.os.Build

val version: Int
    get() = Build.VERSION.SDK_INT

fun fromApi(fromVersion: Int, inclusive: Boolean = true, action: () -> Unit) {
    if (version > fromVersion || (inclusive && version == fromVersion)) action()
}
