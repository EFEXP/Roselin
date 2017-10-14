package xyz.donot.roselinx.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import xyz.donot.roselinx.ui.util.extraUtils.defaultSharedPreferences

class MusicReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        context.defaultSharedPreferences.edit().apply {
            putString("track", bundle.getString("track"))
            putString("artist", bundle.getString("artist"))
            putString("album", bundle.getString("album"))
        }.apply()
    }
   val intentFilter= IntentFilter().apply {
       addAction("com.android.music.metachanged")
       addAction("com.android.music.playstatechanged")
       addAction("com.android.music.playbackcomplete")
   }
}
