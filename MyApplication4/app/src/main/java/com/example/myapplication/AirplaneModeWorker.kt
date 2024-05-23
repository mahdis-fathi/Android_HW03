import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            writeLogToFile(isAirplaneModeEnabled)

            Result.success()
        } catch (e: Exception) {
            Log.e("Airplane_Worker", "Error checking airplane mode", e)
            Result.failure()
        }
    }

    private fun writeLogToFile(airplaneModeStatus: Boolean) {
        val logFile = File(applicationContext.filesDir, "logs.txt")
        val jsonObject = JSONObject()
        jsonObject.put("timestamp", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
            Date(System.currentTimeMillis())
        ))
        jsonObject.put("status of", " airplaneMode is : $airplaneModeStatus")
        val jsonString = jsonObject.toString() + "\n"
        logFile.appendText(jsonString)
    }
}
