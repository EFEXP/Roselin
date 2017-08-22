package xyz.donot.roselin.util.extraUtils

import android.os.Handler
import android.os.Message

fun Handler.post(action: () -> Unit): Boolean = post(Runnable(action))
fun Handler.atFrontOfQueue(action: () -> Unit): Boolean = postAtFrontOfQueue(Runnable(action))
fun Handler.atTime(uptimeMillis: Long, action: () -> Unit): Boolean = postAtTime(Runnable(action), uptimeMillis)
fun Handler.deylaed(delayMillis: Long, action: () -> Unit): Boolean = postDelayed(Runnable(action), delayMillis)
fun handler(handleMessage: (Message) -> Boolean): Handler = android.os.Handler { p0 -> if (p0 == null) false else handleMessage(p0) }
