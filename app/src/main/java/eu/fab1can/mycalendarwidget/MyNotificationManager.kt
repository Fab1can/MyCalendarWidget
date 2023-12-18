package eu.fab1can.mycalendarwidget

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyNotificationManager(private val context: Context) {

    private val channelId = "my_channel_id"
    private val notificationId = 1
    private val handler = Handler(Looper.getMainLooper())
    private val updateIntervalMillis = 20 * 1000L // 20 secondi

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Channel"
            val descriptionText = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            channel.setSound(null, null)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNonDismissableNotification() {
        updateNotificationText()
        handler.postDelayed({
            updateNotificationText()
            showNonDismissableNotification()
        }, updateIntervalMillis)
    }

    fun updateNotificationText() {
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Notifica Dinamica")
            .setContentText("Orario attuale: $currentTime")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSound(null)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationId, builder.build())
        }
    }
}