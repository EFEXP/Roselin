package xyz.donot.roselinx.ui.util.extraUtils

import android.util.Log


class RoselinxConfig {
    companion object {
        var logLevel = Log.ASSERT
        var logEnabled: Boolean
            get() = logLevel < Log.ASSERT
            set(value) {
                logLevel = if (value) Log.VERBOSE else Log.ASSERT
            }
    }
}


fun threadName(): String = Thread.currentThread().name

