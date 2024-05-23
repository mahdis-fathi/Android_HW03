package com.example.myapplication

import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.json.JSONObject
import java.io.File
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

        writeLogToFile(isBluetoothEnabled)
        return Result.success()
    }
    private fun writeLogToFile(bluetoothStatus: Boolean) {
            val logFile = File(applicationContext.filesDir, "logs.txt")
            val jsonObject = JSONObject()
            jsonObject.put("timestamp", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                Date(System.currentTimeMillis())
            ))
            jsonObject.put("status of", "bluetoothStatus is : $bluetoothStatus")
            val jsonString = jsonObject.toString() + "\n"
            logFile.appendText(jsonString)
        }
}