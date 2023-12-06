package com.u1tramarinet.separatedservicesample.lib

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

class MyBroadcastReceiver(private val onReceive: (String) -> Unit) : BroadcastReceiver() {
    companion object {
        private val TAG = MyBroadcastReceiver::class.java.simpleName
    }

    override fun onReceive(context: Context, intent: Intent) {
        val extra: Bundle? = intent.extras
        Log.d(TAG, "onReceive($extra)")
        if (extra != null) {
            val message = extra.getString("message")
            Log.d(TAG, "message=$message")
            if (message != null) {
                onReceive(message)
            }
        }
    }
}