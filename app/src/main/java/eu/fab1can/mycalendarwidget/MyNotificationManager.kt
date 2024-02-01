package eu.fab1can.mycalendarwidget

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import eu.fab1can.mycalendarwidget.calendar.ContactEvent
import eu.fab1can.mycalendarwidget.calendar.Event
import eu.fab1can.mycalendarwidget.tasks.GoogleTasksManager
import pub.devrel.easypermissions.EasyPermissions
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random

class MyNotificationManager(private val service: Service, private val tasks:GoogleTasksManager) {

    private val channelId = "my_channel_id"
    private val channelName = "My Channel"
    private val channelDescription = "Channel Description"
    private val notificationId = 1
    private val handler = Handler(Looper.getMainLooper())
    private val updateIntervalMillis = 120 * 1000L
    private val firstTimeUpdateIntervalMillis = 500L
    private var firstTime = true
    private val random = Random(System.currentTimeMillis()).nextInt()

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
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


    private fun addTasks(notificationView: RemoteViews){
        tasks.updateTaskList()
        for (task in tasks.taskList){
            val taskView = RemoteViews(service.applicationContext.packageName, R.layout.notification_task)
            taskView.setTextViewText(R.id.txtTask, task)
            notificationView.addView(R.id.events_container, taskView)
        }
    }

    private fun addEvents(notificationView:RemoteViews){
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
        if(!EasyPermissions.hasPermissions(service.applicationContext, Manifest.permission.READ_CALENDAR, Manifest.permission.READ_CONTACTS, Manifest.permission.POST_NOTIFICATIONS)){
            return
        }
        //Toast.makeText(service.applicationContext, random.toString(), Toast.LENGTH_SHORT).show()
        //Log.d("gggg", random.toString())
        val notificationView = RemoteViews(service.applicationContext.packageName, R.layout.notification)

        if(tasks.initialized)
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
            //notify(notificationId, builder.build())
            service.startForeground(notificationId, builder.build())
        }
    }
}