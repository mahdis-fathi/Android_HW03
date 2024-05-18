package com.example.myapplication

import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BluetoothStatusWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val bluetoothManager = applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val isBluetoothEnabled = bluetoothAdapter?.isEnabled == true
        Log.i("airplane_worker", "Is Bluetooth enabled? $isBluetoothEnabled")

        fun convertMillisToDate(timestamp: Long): String {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1  // Months are 0-indexed
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val second = calendar.get(Calendar.SECOND)

            return "%d-%02d-%02d %02d:%02d:%02d".format(year, month, day, hour, minute, second)
        }

        fun writeLogToFile(isBluetoothEnabled: Boolean, isAirplaneModeOn: Boolean) {
            val logFile = File(applicationContext.filesDir, "status_logs.txt")
            val timestamp = convertMillisToDate(System.currentTimeMillis())
            val logMessage = "timestamp: $timestamp\n" +
                    "Bluetooth is ${if (isBluetoothEnabled) "Enabled" else "Disabled"}\n" +
                    "Airplane mode is ${if (isAirplaneModeOn) "On" else "Off"}"
            logFile.appendText(logMessage)
        }
        return Result.success()
    }
}