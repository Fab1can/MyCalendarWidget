package eu.fab1can.mycalendarwidget

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import java.util.Calendar


class MyService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()


        val filter = IntentFilter(Intent.ACTION_BOOT_COMPLETED)
        filter.addAction(Intent.ACTION_USER_UNLOCKED)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        val mReceiver: BroadcastReceiver = StartupReceiver()
        registerReceiver(mReceiver, filter)


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MainActivity.myNotificationManager.showNonDismissableNotification(this)
        return Service.START_STICKY
    }

    companion object{
        fun start(context: Context){
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0,
                Intent(context, MyService::class.java), PendingIntent.FLAG_NO_CREATE + PendingIntent.FLAG_IMMUTABLE
            )
            val calendar: Calendar = Calendar.getInstance()
            calendar.setTimeInMillis(System.currentTimeMillis())
            calendar.add(Calendar.MINUTE, 1)

            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                (1000 * 60 * 60).toLong(),
                pendingIntent
            )


            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (service.service.shortClassName.contains("MyService")) {
                    return
                }
            }
            Intent(context, MyService::class.java).also {
                context.startService(it)
            }
        }
    }
}