package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

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
    }
}


