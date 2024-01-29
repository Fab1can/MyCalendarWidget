package eu.fab1can.mycalendarwidget

import android.database.Cursor
import android.net.Uri
import android.Manifest
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import eu.fab1can.mycalendarwidget.calendar.Calendar
import eu.fab1can.mycalendarwidget.calendar.ContactEvent
import eu.fab1can.mycalendarwidget.calendar.Event
import eu.fab1can.mycalendarwidget.databinding.ActivityMainBinding
import pub.devrel.easypermissions.EasyPermissions
import java.util.Date


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener { view ->
            

            val calendars = Calendar.retreiveAll(contentResolver)
            for (calendar in calendars){
                Log.d("ev1", calendar.ID.toString()+":"+calendar.AccName)
            }
            val events=Event.retrieveFutureEvents(contentResolver, arrayOf(1,2,3,5,6,7,8,9,11,13,14,15,16))
            for (event in events){
            }
            val birthdays = ContactEvent.retrieveEvents(contentResolver)
            for(birthday in birthdays){
                Log.d("bb", birthday.Name+":"+birthday.Label+":"+birthday.Date)
            }
        }

        if(!EasyPermissions.hasPermissions(this, Manifest.permission.READ_CALENDAR)){
            EasyPermissions.requestPermissions(this, "", 123, Manifest.permission.READ_CALENDAR)
        }
        if(!EasyPermissions.hasPermissions(this, Manifest.permission.READ_CONTACTS)){
            EasyPermissions.requestPermissions(this, "", 456, Manifest.permission.READ_CONTACTS)
        }

        val n = MyNotificationManager(this)
        n.showNonDismissableNotification()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}