package xyz.donot.roselin.util.extraUtils

import android.util.Log


class QuetzalConfig {
    companion object {
        var logLevel = Log.ASSERT
        var logEnabled: Boolean
            get() {
                return logLevel < Log.ASSERT
            }
            set(value) {
                logLevel = if (value) Log.VERBOSE else Log.ASSERT
            }
    }
}


fun threadName(): String = Thread.currentThread().name

inline fun doIf(condition: Boolean?, action: () -> Unit) {
    if (condition == true) action()
}

inline fun doIf(condition: () -> Boolean?, action: () -> Unit) {
    if (condition() == true ) action()
}

inline fun doIf(any: Any?, action: () -> Unit) {
    if (any != null ) action()
}