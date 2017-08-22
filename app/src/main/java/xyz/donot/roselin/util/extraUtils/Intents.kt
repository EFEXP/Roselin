package xyz.donot.roselin.util.extraUtils

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle

fun flags(flag: Int, vararg flags: Int): Int {
  var orFlags = flag
  for (i in flags) {
    orFlags = orFlags or i
  }
  return orFlags
}

inline fun <reified T : Activity> Activity.start() = startActivity(intent<T>())

inline fun <reified T : Activity> Activity.start(flags: Int) = this.startActivity(intent<T>(flags))

inline fun <reified T: Activity> Activity.start(extras: Bundle) = this.startActivity(intent<T>(extras))

inline fun <reified T: Activity> Activity.start(extras: Bundle, flags: Int) = this.startActivity(intent<T>(extras, flags))


inline fun <reified T : Activity> Activity.startForResult(requestCode: Int) = this.startActivityForResult(intent<T>(), requestCode)

inline fun <reified T : Activity> Activity.startForResult(
  requestCode: Int, flags: Int) = this.startActivityForResult(intent<T>(flags), requestCode)

inline fun <reified T : Activity> Activity.startForResult(
  extras: Bundle, requestCode: Int) = this.startActivityForResult(intent<T>(extras), requestCode)

inline fun <reified T : Activity> Activity.startForResult(
  extras: Bundle, requestCode: Int, flags: Int) = this.startActivityForResult(intent<T>(extras, flags), requestCode)

inline fun <reified T : Activity> Service.start() = this.startActivity(intent<T>(Intent.FLAG_ACTIVITY_NEW_TASK))


inline fun <reified T: Service> Context.start() {
  this.startService(intent<T>())
}
inline fun <reified T: Service> Context.start(flags: Int) {
  this.startService(intent<T>(flags))
}

inline fun <reified T: Service> Context.start(extras: Bundle) {
  this.startService(intent<T>(extras))
}

inline fun <reified T: Service> Context.start(extras: Bundle, flags: Int) {
  this.startService(intent<T>(extras, flags))
}

inline fun <reified T: Context> Context.intent(): Intent = Intent(this, T::class.java)

inline fun <reified T: Context> Context.intent(flags: Int): Intent {
  val intent = intent<T>()
  intent.flags = flags
  return intent
}

inline fun <reified T: Context> Context.intent(extras: Bundle): Intent = intent<T>(extras, 0)

inline fun <reified T: Context> Context.intent(extras: Bundle, flags: Int): Intent {
  val intent = intent<T>(flags)
  intent.putExtras(extras)
  return intent
}
