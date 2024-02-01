package eu.fab1can.mycalendarwidget

import android.app.Service
import android.content.Intent
import android.os.IBinder


class MyNotificationService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val googleTasksManager = MainActivity.googleTasksManager

        val n = MyNotificationManager(this, googleTasksManager)
        n.showNonDismissableNotification()

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
/*
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val intent = Intent("eu.fab1can.mycalendarwidget.APP_CLOSED")
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }*/
}