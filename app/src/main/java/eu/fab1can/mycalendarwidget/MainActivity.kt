package eu.fab1can.mycalendarwidget

import android.database.Cursor
import android.net.Uri
import android.Manifest
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import eu.fab1can.mycalendarwidget.calendar.Calendar
import eu.fab1can.mycalendarwidget.calendar.Event
import eu.fab1can.mycalendarwidget.databinding.ActivityMainBinding
import pub.devrel.easypermissions.EasyPermissions
import java.util.Date


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val CALENDAR_PROJECTION: Array<String> = arrayOf(
        CalendarContract.Calendars._ID
    )
    // The indices for the projection array above.
    private val CALENDAR_PROJECTION_ID_INDEX: Int = 0


    private val EVENT_PROJECTION: Array<String> = arrayOf(
        CalendarContract.Events.CALENDAR_ID,
        CalendarContract.Events.TITLE,
        CalendarContract.Events.DTEND
    )
    private val EVENT_PROJECTION_CALEDNAR_ID_INDEX: Int = 0
    private val EVENT_PROJECTION_TITLE_INDEX: Int = 1
    private val EVENT_PROJECTION_DTEND_INDEX: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener { view ->
            

            val calendars = Calendar.retreiveAll(contentResolver)
            for (calendar in calendars){
                Log.d("ev1", calendar.AccName)
                val events=Event.retrieveEvents("((${CalendarContract.Events.CALENDAR_ID} = ?) AND (${CalendarContract.Events.DTEND} > ${Date().time}))", arrayOf(calendar.ID.toString()), contentResolver, calendar.ID)
                for (event in events){
                    Log.d("ev", event.Title)
                }
            }
        }

        if(!EasyPermissions.hasPermissions(this, Manifest.permission.READ_CALENDAR)){
            EasyPermissions.requestPermissions(this, "", 123, Manifest.permission.READ_CALENDAR)
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