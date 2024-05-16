package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class AirplaneModeWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override fun doWork(): Result {
        val isAirplaneModeEnabled = Settings.Global.getInt(
            applicationContext.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON
        ) != 0

        Log.i("worker_airplane", "Is airplane mode enabled? $isAirplaneModeEnabled")

        return Result.success()
    }
}