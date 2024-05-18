package com.example.myapplication

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

fun saveLogEntry(context: Context, logTime: String, logType: String, logStatus: String) {
    val logEntry = JSONObject()
    logEntry.put("time", logTime)
    logEntry.put("type", logType)
    logEntry.put("status", logStatus)

    val logArray = JSONArray()
    logArray.put(logEntry)

    val logsObject = JSONObject()
    logsObject.put("logs", logArray)

    val logsDirectory = File(context.filesDir, "logs")
    logsDirectory.mkdirs()

    val logFile = File(logsDirectory, "log_file.json")
    val logWriter = FileWriter(logFile, true)

    logWriter.use {
        it.write(logsObject.toString())
        it.write("\n")
    }
}