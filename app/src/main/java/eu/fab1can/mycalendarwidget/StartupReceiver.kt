package eu.fab1can.mycalendarwidget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class StartupReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "ciao", Toast.LENGTH_SHORT).show()
        MyService.start(context)
    }
}