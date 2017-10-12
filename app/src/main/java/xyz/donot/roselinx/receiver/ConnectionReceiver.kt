package xyz.donot.roselinx.receiver

import android.arch.lifecycle.MutableLiveData
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class ConnectionReceiver : BroadcastReceiver() {
    val isConnectedStream = MutableLiveData<Boolean>()
    val intentFilter= IntentFilter("connection")
    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        val connection = bundle.getBoolean("connection")
        isConnectedStream.value = connection
    }
}