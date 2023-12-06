package com.u1tramarinet.separatedservicesample.lib

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
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
    private val callbackSet: MutableSet<ICallback> = mutableSetOf()
    private lateinit var receiver: MyBroadcastReceiver

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate() pid=${Process.myPid()}")
        receiver = MyBroadcastReceiver(onReceive = { message ->
            Log.d(TAG, "onReceive($message)")
            callbackSet.forEach {
                it.onEvent(message)
            }
        })
        val intentFilter =
            IntentFilter("com.u1tramarinet.separatedservicesample.lib.receiver")
        registerReceiver(receiver, intentFilter, RECEIVER_EXPORTED)
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind()")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
        unregisterReceiver(receiver)
    }

    inner class RemoteBinder : IRemoteSeparatedService.Stub() {
        override fun getRandomNumber(): Int {
            Log.d(TAG, "getRandomNumber()")
            return generator.nextInt()
        }

        override fun registerCallback(callback: ICallback?) {
            Log.d(TAG, "registerCallback($callback)")
            if (callback != null) {
                callbackSet.add(callback)
            }
        }

        override fun unregisterCallback(callback: ICallback?) {
            Log.d(TAG, "unregisterCallback($callback)")
            if (callback != null) {
                callbackSet.remove(callback)
            }
        }
    }
}