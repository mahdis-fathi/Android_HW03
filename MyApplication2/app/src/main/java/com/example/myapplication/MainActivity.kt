package com.example.myapplication

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.util.concurrent.TimeUnit



@Suppress("PreviewNotSupportedInUnitTestFiles")
class MainActivity : ComponentActivity() {

    private var workManager: WorkManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }

        workManager = WorkManager.getInstance(applicationContext)

        // Set up periodic work for AirplaneModeWorker
        val airplaneModeWorkRequest = PeriodicWorkRequestBuilder<AirplaneModeWorker>(
            repeatInterval = 2, TimeUnit.MINUTES
        ).build()
        workManager?.enqueue(airplaneModeWorkRequest)

        // Set up periodic work for BluetoothStatusWorker
        val bluetoothStatusWorkRequest = PeriodicWorkRequestBuilder<BluetoothStatusWorker>(
            repeatInterval = 2, TimeUnit.MINUTES
        ).build()
        workManager?.enqueue(bluetoothStatusWorkRequest)
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        MyApplicationTheme {
            Greeting("Android")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //unregisterReceiver(airplaneModeReceive)
    }
}