package eu.fab1can.mycalendarwidget

import android.app.Application
import android.os.Handler
import androidx.work.WorkInfo
import androidx.work.WorkManager
import eu.fab1can.mycalendarwidget.MainActivity
import eu.fab1can.mycalendarwidget.MyNotificationManager
import eu.fab1can.mycalendarwidget.tasks.GoogleTasksManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        MainActivity.myNotificationManager = MyNotificationManager()
        MainActivity.myNotificationManager.context=this
        MainActivity.myNotificationManager.createNotificationChannel()


    }
    private fun isBackgroundRunning(): Boolean {
        val works = WorkManager.getInstance(applicationContext).getWorkInfosByTag("notificationWork").get()
        for (work in works) {
            if (work.state== WorkInfo.State.RUNNING||work.state== WorkInfo.State.ENQUEUED) {
                return true
            }
        }
        return false
    }
}