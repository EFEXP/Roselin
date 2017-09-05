package xyz.donot.roselin.util.extraUtils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

private val uiHandler = Handler(Looper.getMainLooper())

/**
 * Executes the provided code immediately on the UI Thread
 *
 */
 fun mainThread(runnable: () -> Unit) {
  uiHandler.post(runnable)
}

