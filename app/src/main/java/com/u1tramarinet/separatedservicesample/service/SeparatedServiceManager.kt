package com.u1tramarinet.separatedservicesample.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference

class SeparatedServiceManager(
    context: Context,
    finishWhenOnStop: Boolean = false,
) {
    private var separatedService: SeparatedService? = null
    private var bound: Boolean = false
    private val contextRef: WeakReference<Context>
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(
                SeparatedServiceManager::class.java.simpleName,
                "onServiceConnected($name, $service)"
            )
            val binder = service as SeparatedService.LocalBinder
            separatedService = binder.getService()
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(SeparatedServiceManager::class.java.simpleName, "onServiceDisconnected($name)")
            bound = false
            separatedService = null
            doBindIfAvailable()
        }
    }
    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            Log.d(SeparatedServiceManager::class.java.simpleName, "onStart(owner=$owner)")
            super.onStart(owner)
            doBindIfAvailable()
        }

        override fun onStop(owner: LifecycleOwner) {
            Log.d(SeparatedServiceManager::class.java.simpleName, "onStop(owner=$owner)")
            super.onStop(owner)
            finish()
        }
    }

    init {
        Log.d(SeparatedServiceManager::class.java.simpleName, "context=$context, finishWhenOnStop=$finishWhenOnStop")
        contextRef = WeakReference(context)
        if (finishWhenOnStop) {
            assert(context is LifecycleOwner)
            if (context is LifecycleOwner) {
                Log.d(SeparatedServiceManager::class.java.simpleName, "addObserver")
                context.lifecycle.addObserver(lifecycleObserver)
            }
        } else {
            doBindIfAvailable()
        }
    }

    fun getRandomNumber(): Int {
        Log.d(SeparatedServiceManager::class.java.simpleName, "getRandomNumber()")
        if (!bound || separatedService == null) {
            return 0
        }
        return separatedService?.randomNumber ?: 0
    }

    fun finish() {
        Log.d(SeparatedServiceManager::class.java.simpleName, "finish()")
        val context: Context? = contextRef.get()
        if (context != null) {
            Log.d(SeparatedServiceManager::class.java.simpleName, "unbindService()")
            context.unbindService(connection)
        }
    }

    private fun doBindIfAvailable() {
        Log.d(SeparatedServiceManager::class.java.simpleName, "doBindIfAvailable()")
        val context: Context? = contextRef.get()
        if (context != null) {
            Log.d(SeparatedServiceManager::class.java.simpleName, "bindService()")
            Intent(context, SeparatedService::class.java).also { intent ->
                context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }
}