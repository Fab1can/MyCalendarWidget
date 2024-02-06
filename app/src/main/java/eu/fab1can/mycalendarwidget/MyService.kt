package eu.fab1can.mycalendarwidget

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MyService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MainActivity.myNotificationManager.showNonDismissableNotification(this)
        return super.onStartCommand(intent, flags, startId)
    }
}