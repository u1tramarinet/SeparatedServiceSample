package com.u1tramarinet.separatedservicesample.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlin.random.Random

class SeparatedService : Service() {
    private val binder = LocalBinder()
    private val generator = Random(1L)

    val randomNumber: Int
        get() {
            Log.d(SeparatedService::class.java.simpleName, "get randomNumber")
            return generator.nextInt()
        }

    override fun onCreate() {
        super.onCreate()
        Log.d(SeparatedService::class.java.simpleName, "onCreate()")
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(SeparatedService::class.java.simpleName, "onBind()")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(SeparatedService::class.java.simpleName, "onUnbind")
        return super.onUnbind(intent)
    }

    inner class LocalBinder : Binder() {
        fun getService(): SeparatedService = this@SeparatedService
    }
}