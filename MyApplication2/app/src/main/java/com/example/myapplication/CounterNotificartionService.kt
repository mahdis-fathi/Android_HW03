package com.example.myapplication

import android.content.Context

class CounterNotificartionService(
    private val context: Context
) {

    companion object {
        const val INTERNET_CHANNEL_ID = "internet_channel"
        const val COUNTER_CHANNEL_ID = "counter_channel"
    }
}