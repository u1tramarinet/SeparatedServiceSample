package com.u1tramarinet.separatedservicesample.lib

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference

class RemoteSeparatedServiceManager(
    context: Context,
    finishWhenOnStop: Boolean = false,
) {
    companion object {
        private val TAG: String = RemoteSeparatedServiceManager::class.java.simpleName
    }

    private var remoteSeparatedService: IRemoteSeparatedService? = null
    private var bound: Boolean = false
    private val contextRef: WeakReference<Context>
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(
                TAG,
                "onServiceConnected($name, $service)"
            )
            remoteSeparatedService = IRemoteSeparatedService.Stub.asInterface(service)
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected($name)")
            bound = false
            remoteSeparatedService = null
            doBindIfAvailable()
        }
    }
    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            Log.d(TAG, "onStart(owner=$owner)")
            super.onStart(owner)
            doBindIfAvailable()
        }

        override fun onStop(owner: LifecycleOwner) {
            Log.d(TAG, "onStop(owner=$owner)")
            super.onStop(owner)
            finish()
        }
    }

    init {
        Log.d(TAG, "context=$context, finishWhenOnStop=$finishWhenOnStop")
        contextRef = WeakReference(context)
        if (finishWhenOnStop) {
            assert(context is LifecycleOwner)
            if (context is LifecycleOwner) {
                Log.d(TAG, "addObserver")
                context.lifecycle.addObserver(lifecycleObserver)
            }
        } else {
            doBindIfAvailable()
        }
    }

    fun getRandomNumber(): Int {
        Log.d(TAG, "getRandomNumber()")
        if (!bound || remoteSeparatedService == null) {
            return 0
        }
        return remoteSeparatedService?.randomNumber ?: 0
    }

    fun finish() {
        Log.d(TAG, "finish()")
        val context: Context? = contextRef.get()
        if (context != null) {
            Log.d(TAG, "unbindService()")
            context.unbindService(connection)
        }
    }

    private fun doBindIfAvailable() {
        Log.d(TAG, "doBindIfAvailable()")
        val context: Context? = contextRef.get()
        if (context != null) {
            Log.d(TAG, "bindService()")
            Intent(context, RemoteSeparatedService::class.java).also { intent ->
                context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }
}