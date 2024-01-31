package eu.fab1can.mycalendarwidget

import android.Manifest
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import eu.fab1can.mycalendarwidget.tasks.GoogleTasksManager
import pub.devrel.easypermissions.EasyPermissions


class MyNotificationService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val googleTasksManager = MainActivity.googleTasksManager

        val n = MyNotificationManager(this, googleTasksManager)
        n.showNonDismissableNotification()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}