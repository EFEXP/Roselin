package xyz.donot.roselinx.util.extraUtils
import android.os.Build

val version: Int
    get() = Build.VERSION.SDK_INT

fun toApi(toVersion: Int, inclusive: Boolean = false, action: () -> Unit) {
    if (version < toVersion || (inclusive && version == toVersion)) action()
}

fun fromApi(fromVersion: Int, inclusive: Boolean = true, action: () -> Unit) {
    if (version > fromVersion || (inclusive && version == fromVersion)) action()
}
