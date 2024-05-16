package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val capabilities =
                connectivityManager.getNetworkCapabilities(network)

            val isConnected = capabilities != null &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

            if (isConnected) {
                showInternetStatusNotification(context, "Internet Connected")
            } else {
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
}