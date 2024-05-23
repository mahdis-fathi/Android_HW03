package com.example.myapplication

import AirplaneModeWorker
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext


@Suppress("PreviewNotSupportedInUnitTestFiles")
class MainActivity : ComponentActivity() {

    val connectivityReceiver = ConnectivityReceiver()
    private var workManager: WorkManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DisplayWorkerData(LocalContext.current)
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

    }
    @Composable
    fun DisplayWorkerData(context: Context) {
        val workerData = readWorkerDataFromFile(context)

        LazyColumn {
            items(workerData) { data ->
                WorkerDataItem(data)
            }
        }
    }

    @Composable
    fun WorkerDataItem(data: WorkerData) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Timestamp: ${data.timestamp}")
            Text("${data.status}")
        }
    }

    private fun readWorkerDataFromFile(context: Context, ): List<WorkerData> {
        val workerData = mutableListOf<WorkerData>()

        // Read the data from the text file
        val file = File(context.filesDir, "logs.txt")
        file.forEachLine { line ->
            val json = JSONObject(line)
            val data = WorkerData(
                timestamp = json.getString("timestamp"),
                status = json.getString("status of"),
            )
            workerData.add(data)
        }

        return workerData
    }

    private fun getBatteryLevel(): Int {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            applicationContext.registerReceiver(null, ifilter)
        }
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return (level * 100 / scale.toFloat()).toInt()
    }

    @SuppressLint("MissingPermission")
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
        Log.i("battery_notification", "Sending battery notification with level $batteryLevel%")
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    @Composable
    fun LogEntry(entry: JSONObject) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = entry.getString("timestamp"),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = entry.getString("internet status is :"),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(connectivityReceiver)
    }
}

data class WorkerData(
    val timestamp: String,
    val status: String,
)