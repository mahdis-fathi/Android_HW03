package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.io.File
import java.util.Calendar

class AirplaneModeWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    @SuppressLint("NewApi")
    override suspend fun doWork(): Result {
        return try {
            // Check Airplane Mode
            val isAirplaneModeEnabled = Settings.Global.getInt(
                applicationContext.contentResolver,
                Settings.Global.AIRPLANE_MODE_ON
            ) != 0
            Log.i("Airplane_Worker", "Is airplane mode enabled? $isAirplaneModeEnabled")

            Result.success()
        } catch (e: Exception) {
            Log.e("Airplane_Worker", "Error checking airplane mode", e)
            Result.failure()
        }
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

    }
}
