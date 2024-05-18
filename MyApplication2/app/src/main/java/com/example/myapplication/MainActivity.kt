package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.Bundle
import android.os.FileObserver
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit



@Suppress("PreviewNotSupportedInUnitTestFiles")
class MainActivity : ComponentActivity() {

    val connectivityReceiver = ConnectivityReceiver()
    private var workManager: WorkManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerReceiver(
            connectivityReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )



        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LogDisplayScreen(this)
                    //Greeting("Android")
                }
            }
        }


        val batteryLevel = getBatteryLevel()
        sendBatteryNotification(batteryLevel)

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


        try {
            val context = applicationContext
            val filename = "logcat_" + System.currentTimeMillis() + ".txt"
            val outputFile = File(context?.externalCacheDir, filename)
            Runtime.getRuntime().exec("logcat -f" + outputFile.absolutePath)
        } catch (e: Exception) {
        }

    }

    private fun getBatteryLevel(): Int {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            applicationContext.registerReceiver(null, ifilter)
        }
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return (level * 100 / scale.toFloat()).toInt()
    }

    private fun sendBatteryNotification(batteryLevel: Int) {
        val channelId = CounterNotificartionService.COUNTER_CHANNEL_ID
        val notificationId = 1

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.baseline_battery_unknown_24)
            .setContentTitle("Battery Level")
            .setContentText("Current battery level: $batteryLevel%")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    fun readLogsFromFile(context: Context): List<String> {
        val logFile = File(context.filesDir, "status_logs.txt")
        val logs = mutableListOf<String>()
        if (logFile.exists()) {
            val lines = logFile.readLines()
            lines.forEach { line ->
                try {
                    val jsonObject = JSONObject(line)
                    val timestamp = jsonObject.getString("timestamp")
                    val bluetoothEnabled = jsonObject.getBoolean("bluetooth_enabled")
                    val airplaneModeOn = jsonObject.getBoolean("airplane_mode_on")

                    val logMessage = buildString {
                        appendLine("time: $timestamp")
                        appendLine("Bluetooth is ${if (bluetoothEnabled) "Enabled" else "Disabled"}")
                        appendLine("Airplane mode is ${if (airplaneModeOn) "On" else "Off"}")
                    }
                    logs.add(logMessage)
                } catch (e: JSONException) {
                    // Handle potential parsing errors from the JSON
                    Log.e("readLogsFromFile", "Error parsing log line: $line", e)
                }
            }
        }
        return logs.reversed()
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

        unregisterReceiver(connectivityReceiver)

        //unregisterReceiver(airplaneModeReceive)

    }

    class LogFileObserver(
        private val context: Context,
        private val callback: () -> Unit
    ) : FileObserver(context.filesDir.path + "/status_logs.txt", CREATE or MODIFY) {

        override fun onEvent(event: Int, path: String?) {
            if (event == CREATE || event == MODIFY) {
                callback()
            }
        }
    }
    @Composable
    fun LogDisplayScreen(context: Context) {
        val logs = remember { mutableStateOf(readLogsFromFile(context)) }

        val observer = remember {
            LogFileObserver(context) {
                logs.value = readLogsFromFile(context)
            }
        }

        DisposableEffect(context) {
            observer.startWatching()
            onDispose {
                observer.stopWatching()
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
        ) {
            // Use the size of the logs list for the number of items
            items(logs.value.size) { index ->  // Access each item by index
                val log = logs.value[index]  // Get the log entry at the current index
                Text(text = log)
                Divider()
            }
        }
    }


}


