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
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
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

<<<<<<< Updated upstream
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
=======

        val logTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val logType = "Bluetooth"
        val logStatus = "Is Bluetooth enabled? $isBluetoothEnabled"
        saveLogEntry(applicationContext, logTime, logType, logStatus)
        Log.i("airplane_worker", "Is Bluetooth enabled? $isBluetoothEnabled")
>>>>>>> Stashed changes
        return Result.success()
    }

    private fun saveLogEntry(context: Context, logTime: String, logType: String, logStatus: String) {
        val logEntry = JSONObject()
        logEntry.put("time", logTime)
        logEntry.put("type", logType)
        logEntry.put("status", logStatus)

        val logsDirectory = File(context.filesDir, "logs")
        if (!logsDirectory.exists()) {
            val isDirectoryCreated = logsDirectory.mkdirs()
            if (!isDirectoryCreated) {
                Log.e("BluetoothStatusWorker", "Error creating logs directory")
                return
            }
        }

        val logFile = File(logsDirectory, "log_file.json")
        if (!logFile.exists()) {
            try {
                val isFileCreated = logFile.createNewFile()
                if (!isFileCreated) {
                    Log.e("BluetoothStatusWorker", "Error creating log file")
                    return
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("BluetoothStatusWorker", "Error creating log file: ${e.message}")
                return
            }
        }

        val logArray = if (logFile.exists()) {
            try {
                val existingLogs = logFile.readText()
                if (existingLogs.isNotEmpty()) {
                    try {
                        JSONArray(existingLogs)
                    } catch (ex: Exception) {
                        JSONArray()
                    }
                } else {
                    JSONArray()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("BluetoothStatusWorker", "Error reading log file: ${e.message}")
                return
            }
        } else {
            JSONArray()
        }

        logArray.put(logEntry)

        try {
            FileWriter(logFile).use { fileWriter ->
                fileWriter.write(logArray.toString())
                fileWriter.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("BluetoothStatusWorker", "Error writing to log file: ${e.message}")
        }
    }
}