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
    private var disconnecting: Boolean = false
    private val contextRef: WeakReference<Context>
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(
                TAG,
                "onServiceConnected($name, $service)"
            )
            remoteSeparatedService = IRemoteSeparatedService.Stub.asInterface(service)
            bound = true
            if (pendingCallbacks.isNotEmpty()) {
                pendingCallbacks.forEach { callback ->
                    registerCallback(callback)
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected($name)")
            bound = false
            remoteSeparatedService = null
            if (!disconnecting) {
                doBindIfAvailable()
                registerCallbacks()
            }
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
            disconnect()
        }
    }
    private val callbackMap: MutableMap<Callback, ICallback.Stub> = mutableMapOf()
    private val pendingCallbacks: MutableSet<Callback> = mutableSetOf()

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

    fun disconnect() {
        Log.d(TAG, "disconnect()")
        if (disconnecting) return
        val context: Context? = contextRef.get()
        if (context != null) {
            Log.d(TAG, "unbindService()")
            context.unbindService(connection)
            disconnecting = true
        }
    }

    fun registerCallback(callback: Callback) {
        Log.d(TAG, "registerCallback($callback)")
        if (callbackMap.containsKey(callback)) {
            return
        }
        if (remoteSeparatedService != null) {
            val callbackToService = wrapCallback(callback)
            remoteSeparatedService?.registerCallback(callbackToService)
            callbackMap[callback] = callbackToService
            if (pendingCallbacks.contains(callback)) {
                pendingCallbacks.remove(callback)
            }
        } else {
            pendingCallbacks.add(callback)
        }
    }

    fun unregisterCallback(callback: Callback) {
        Log.d(TAG, "unregisterCallback($callback)")
        val callbackToService = callbackMap[callback] ?: return
        remoteSeparatedService?.unregisterCallback(callbackToService)
        callbackMap.remove(callback)
    }

    private fun registerCallbacks() {
        callbackMap.forEach { (_, callback) -> remoteSeparatedService?.registerCallback(callback) }
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

    private fun wrapCallback(internal: Callback): ICallback.Stub {
        return object : ICallback.Stub() {
            override fun onEvent(message: String?) {
                Log.d(TAG, "onEvent($message)")
                internal.onEvent(message ?: "")
            }
        }
    }

    interface Callback {
        fun onEvent(message: String)
    }
}