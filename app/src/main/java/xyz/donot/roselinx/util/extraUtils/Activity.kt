package xyz.donot.roselinx.util.extraUtils
import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment as SupportFragment



fun Activity.restart(builder: Intent.() -> Unit = {}) {
    val i = Intent(this, this::class.java)
    i.putExtras(intent.extras)
    i.builder()
    startActivity(i)
    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out) //No transitions
    finish()
    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
}