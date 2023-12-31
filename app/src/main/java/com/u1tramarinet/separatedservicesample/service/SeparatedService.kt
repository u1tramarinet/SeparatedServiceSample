package com.u1tramarinet.separatedservicesample.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Process
import android.util.Log
import kotlin.random.Random

class SeparatedService : Service() {
    companion object {
        private val TAG = SeparatedService::class.java.simpleName
    }

    private val binder = LocalBinder()
    private val generator = Random(1L)

    val randomNumber: Int
        get() {
            Log.d(TAG, "get randomNumber")
            return generator.nextInt()
        }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate() pid=${Process.myPid()}")
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind()")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    inner class LocalBinder : Binder() {
        fun getService(): SeparatedService = this@SeparatedService
    }
}