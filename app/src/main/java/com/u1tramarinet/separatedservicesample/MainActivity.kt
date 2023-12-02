package com.u1tramarinet.separatedservicesample

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.u1tramarinet.separatedservicesample.ui.theme.SeparatedServiceSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeparatedServiceSampleTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        "Android",
                        onClick = { startSubActivity() },
                    )
                }
            }
        }
    }

    private fun startSubActivity() {
        Intent(this, SubActivity::class.java).also { intent ->
            startActivity(intent)
        }
    }
}

@Composable
fun MainScreen(
    name: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(modifier = modifier) {
        Text(
            text = "Hello $name!",
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onClick.invoke() }) {
            Text(
                text = "Go to SubActivity",
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SeparatedServiceSampleTheme {
        MainScreen("Android")
    }
}