package com.example.myapplication

import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class BluetoothStatusWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val bluetoothManager = applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val isBluetoothEnabled = bluetoothAdapter?.isEnabled == true
        Log.i("airplane_worker", "Is Bluetooth enabled? $isBluetoothEnabled")

        return Result.success()
    }
}