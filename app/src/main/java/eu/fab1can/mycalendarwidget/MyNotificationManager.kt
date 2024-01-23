package eu.fab1can.mycalendarwidget

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import eu.fab1can.mycalendarwidget.calendar.Calendar
import eu.fab1can.mycalendarwidget.calendar.Event
import java.util.Date

class MyNotificationManager(private val context: Context) {

    private val channelId = "my_channel_id"
    private val notificationId = 1
    private val handler = Handler(Looper.getMainLooper())
    private val updateIntervalMillis = 20 * 1000L // 20 secondi
    private var text = ""

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
        val events = Event.retrieveFutureEvents(context.contentResolver, arrayOf(1,2,3,5,6,7,8,9,11,13,14,15,16))
        text=""
        for (event in events){
            text=text+"ev:"+event.Title+"\n"
        }
        Log.d("xxx", text)
        val extender = NotificationCompat.WearableExtender().setStartScrollBottom(true)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Notifica Dinamica")
            .setStyle(
                NotificationCompat.BigTextStyle()
                .bigText(text))
            .extend(extender)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
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