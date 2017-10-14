package xyz.donot.roselinx.ui.util.extraUtils
import android.os.Handler
import android.os.Looper

private val uiHandler = Handler(Looper.getMainLooper())

/**
 * Executes the provided code immediately on the UI Thread
 *
 */
 fun mainThread(runnable: () -> Unit) {
  uiHandler.post(runnable)
}

