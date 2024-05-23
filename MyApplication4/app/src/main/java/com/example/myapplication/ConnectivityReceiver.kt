package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("internet_notification", "onReceive: Connectivity change detected")
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            Log.i("internet_notification", "onReceive:Connectivity action received")
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val capabilities =
                connectivityManager.getNetworkCapabilities(network)

            val isConnected = capabilities != null &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

            if (isConnected) {
                Log.i("internet_notification", "onReceive: Internet connected")
                writeLogToFile(context,"connected")
                showInternetStatusNotification(context, "Internet Connected")
            } else {
                Log.i("internet_notification", "onReceive: Internet disconnected")
                writeLogToFile(context,"disconnected")
                showInternetStatusNotification(context, "Internet Disconnected")
            }
        }
    }

    private fun showInternetStatusNotification(context: Context, status: String) {
        val channelId = CounterNotificartionService.COUNTER_CHANNEL_ID
        val notificationId = 2

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.baseline_signal_wifi_statusbar_connected_no_internet_4_24)
            .setContentTitle("Internet Status")
            .setContentText(status)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
    private fun writeLogToFile(context: Context, internetStatus: String) {
        val logFile = File(context.filesDir, "logs.txt")
        val jsonObject = JSONObject()
        jsonObject.put("timestamp", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
            Date(System.currentTimeMillis())
        ))
        jsonObject.put("status of", "internet status is : " + internetStatus)
        val jsonString = jsonObject.toString() + "\n"
        logFile.appendText(jsonString)
    }
}