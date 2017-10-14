package xyz.donot.roselinx.ui.util.extraUtils

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle

inline fun <reified T : Context> Context.newIntent(): Intent =
        Intent(this, T::class.java)

inline fun <reified T : Context> Context.newIntent(flags: Int): Intent {
    val intent = newIntent<T>()
    intent.flags = flags
    return intent
}

inline fun <reified T : Context> Context.newIntent(extras: Bundle): Intent = newIntent<T>(0, extras)

inline fun <reified T : Context> Context.newIntent(flags: Int, extras: Bundle): Intent {
    val intent = newIntent<T>(flags)
    intent.putExtras(extras)
    return intent
}

inline fun <reified T : Activity> Activity.startActivity()=
        this.startActivity(newIntent<T>(),android.support.v4.app.ActivityOptionsCompat.makeSceneTransitionAnimation(this,null).toBundle())

inline fun <reified T : Activity> Activity.startActivity(flags: Int) =
        this.startActivity(newIntent<T>(flags),android.support.v4.app.ActivityOptionsCompat.makeSceneTransitionAnimation(this,null).toBundle())

inline fun <reified T : Activity> Activity.startActivity(extras: Bundle) =
        this.startActivity(newIntent<T>(extras),android.support.v4.app.ActivityOptionsCompat.makeSceneTransitionAnimation(this,null).toBundle())

inline fun <reified T : Activity> Activity.startActivity(flags: Int, extras: Bundle) =
        this.startActivity(newIntent<T>(flags, extras),android.support.v4.app.ActivityOptionsCompat.makeSceneTransitionAnimation(this,null).toBundle())

inline fun <reified T : Service> Context.startService(): ComponentName =
        this.startService(newIntent<T>())

