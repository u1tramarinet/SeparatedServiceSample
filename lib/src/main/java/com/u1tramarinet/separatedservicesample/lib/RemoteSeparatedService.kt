package com.u1tramarinet.separatedservicesample.lib

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import android.util.Log
import kotlin.random.Random

class RemoteSeparatedService : Service() {
    companion object {
        private val TAG = RemoteSeparatedService::class.java.simpleName
    }

    private val binder = RemoteBinder()
    private val generator = Random(2L)

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

    inner class RemoteBinder : IRemoteSeparatedService.Stub() {
        override fun getRandomNumber(): Int {
            Log.d(TAG, "get randomNumber")
            return generator.nextInt()
        }
    }
}