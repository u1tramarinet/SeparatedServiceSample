package com.u1tramarinet.separatedservicesample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.u1tramarinet.separatedservicesample.lib.RemoteSeparatedServiceManager
import com.u1tramarinet.separatedservicesample.service.SeparatedServiceManager
import com.u1tramarinet.separatedservicesample.ui.theme.SeparatedServiceSampleTheme

class SubActivity : ComponentActivity() {
    private var manager: SeparatedServiceManager? = null
    private var remoteManager: RemoteSeparatedServiceManager? = null
    private var callback = object : RemoteSeparatedServiceManager.Callback {
        override fun onEvent(message: String) {
            Log.d(SubActivity::class.java.simpleName, "onEvent($message)")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(SubActivity::class.java.simpleName, "onCreate()")
        super.onCreate(savedInstanceState)
        setContent {
            SeparatedServiceSampleTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SubScreen("Android", onClick = { getRandomNumber() })
                }
            }
        }
    }

    override fun onStart() {
        Log.d(SubActivity::class.java.simpleName, "onStart()")
        super.onStart()
        manager = SeparatedServiceManager(this, true)
        remoteManager = RemoteSeparatedServiceManager(this, true)
        remoteManager?.registerCallback(callback = callback)
    }

    override fun onStop() {
        Log.d(SubActivity::class.java.simpleName, "onStop()")
        super.onStop()
        manager = null
        remoteManager?.unregisterCallback(callback)
        remoteManager = null
    }

    private fun getRandomNumber() {
        val number = manager?.getRandomNumber() ?: -1
        val remoteNumber = remoteManager?.getRandomNumber() ?: -1
        Log.d(
            SubActivity::class.java.simpleName,
            "random number=$number, random number(remote)=$remoteNumber"
        )
        Toast.makeText(this, "Got random number: $number/$remoteNumber", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun SubScreen(
    name: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = "Hello $name!",
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onClick.invoke() }) {
            Text(
                text = "Get random number",
            )
        }
    }
}