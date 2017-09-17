package xyz.donot.roselinx.util.extraUtils
import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment as SupportFragment


fun Activity.restart() {
    val intent = this.intent
    this.overridePendingTransition(0, 0)
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    this.finish()
    this.overridePendingTransition(0, 0)
    this.startActivity(intent)
}
