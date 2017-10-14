package xyz.donot.roselinx.ui.util.extraUtils
import android.app.Activity
import android.support.v4.app.Fragment as SupportFragment



fun Activity.restart() {
    val intent = this.intent
    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out) //No transitions
    finish()
    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
    startActivity(intent)
}