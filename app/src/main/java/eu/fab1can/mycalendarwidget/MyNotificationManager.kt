package eu.fab1can.mycalendarwidget

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import eu.fab1can.mycalendarwidget.calendar.ContactEvent
import eu.fab1can.mycalendarwidget.calendar.Event
import eu.fab1can.mycalendarwidget.tasks.GoogleTasksManager
import java.time.LocalDate
import java.time.LocalDateTime

class MyNotificationManager(private val service: Service, private val tasks:GoogleTasksManager) {

    private val channelId = "my_channel_id"
    private val notificationId = 1
    private val handler = Handler(Looper.getMainLooper())
    private val updateIntervalMillis = 2 * 1000L // 20 secondi
    private val firstTimeUpdateIntervalMillis = 1000L // 1 secondo
    private var firstTime = true
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
                service.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNonDismissableNotification() {
        updateNotificationText()
        handler.postDelayed({
            firstTime=false
            updateNotificationText()
            showNonDismissableNotification()
        }, if(firstTime) firstTimeUpdateIntervalMillis else updateIntervalMillis)
    }


    fun addTasks(notificationView: RemoteViews){
        tasks.updateTaskList()
        for (task in tasks.taskList){
            val taskView = RemoteViews(service.applicationContext.packageName, R.layout.notification_task)
            taskView.setTextViewText(R.id.txtTask, task)
            notificationView.addView(R.id.events_container, taskView)
        }
    }

    fun addEvents(notificationView:RemoteViews){
        val events = Event.retrieveFutureEvents(service.applicationContext.contentResolver, arrayOf(1,2,3,5,6,7,8,9,11,13,14,15,16))
        val contactEvents = ContactEvent.retrieveNextEvents(service.applicationContext.contentResolver)

        val allEvents = arrayOf(*events,*contactEvents).sortedBy { it->it.currentYearDateTime() }
        for (event in allEvents){
            val DtStart : LocalDateTime
            val Title : String
            val BackgroundColor : Int
            val AllDay : Boolean
            val TextColor : Int
            val DtEnd : LocalDateTime

            if(event is Event){
                val ev = event as Event
                DtStart = ev.DtStart
                Title = ev.Title
                TextColor = ContextCompat.getColor(service.applicationContext,android.R.color.system_accent1_500)
                AllDay = ev.AllDay
                DtEnd = ev.DtEnd
            }else{
                val ev = event as ContactEvent
                DtStart = ev.Date.atStartOfDay()
                if(ev.HasYear){
                    Title = ev.Label+", "+ev.Name+", "+(LocalDate.now().year-ev.Date.year)
                }else{
                    Title = ev.Label+", "+ev.Name
                }
                TextColor = ContextCompat.getColor(service.applicationContext, android.R.color.holo_green_light)
                AllDay = true
                DtEnd = LocalDateTime.MAX
            }


            val eventView = RemoteViews(service.applicationContext.packageName, R.layout.notification_event)
            eventView.setTextViewText(R.id.txtEvent, Title)
            if(DtStart.toLocalDate().isEqual(LocalDate.now())){
                eventView.setInt(R.id.event_border, "setBackgroundColor", ContextCompat.getColor(service.applicationContext,com.google.android.material.R.color.design_default_color_secondary))
            }
            var timeText : String
            if(AllDay){
                timeText = DtStart.dayOfWeek.name
            }else{
                timeText = DtStart.dayOfWeek.name+", "+DtStart.hour+":"+String.format("%02d",DtStart.minute)+"-"+DtEnd.hour+":"+String.format("%02d",DtEnd.minute)
            }
            eventView.setInt(R.id.txtTime, "setTextColor", TextColor)

            eventView.setTextViewText(R.id.txtTime, timeText)
            notificationView.addView(R.id.events_container, eventView)
        }
    }

    fun updateNotificationText() {

        val notificationView = RemoteViews(service.applicationContext.packageName, R.layout.notification)

        addTasks(notificationView)
        addEvents(notificationView)

        val builder = NotificationCompat.Builder(service.applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setCustomContentView(notificationView)
            .setCustomBigContentView(notificationView)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setSound(null)

        with(NotificationManagerCompat.from(service.applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    service.applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationId, builder.build())
        }
    }
}