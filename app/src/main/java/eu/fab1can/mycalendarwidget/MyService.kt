package eu.fab1can.mycalendarwidget

import android.app.ActivityManager
import android.app.AlertDialog
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.IBinder
import eu.fab1can.mycalendarwidget.tasks.GoogleTasksManager


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