package eu.fab1can.mycalendarwidget

import android.database.Cursor
import android.net.Uri
import android.Manifest
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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


            // Run query
            val uriCalendar: Uri = CalendarContract.Calendars.CONTENT_URI
            val selectionCalendar: String = "((${CalendarContract.Calendars.ACCOUNT_NAME} = ?) AND (" +
                    "${CalendarContract.Calendars.ACCOUNT_TYPE} = ?) AND (" +
                    "${CalendarContract.Calendars.OWNER_ACCOUNT} = ?))"
            val selectionArgsCalendar: Array<String> = arrayOf("fabiocan7@gmail.com", "com.google", "fabiocan7@gmail.com")
            val curCalendar: Cursor =
                contentResolver.query(uriCalendar, CALENDAR_PROJECTION, selectionCalendar, selectionArgsCalendar, null)!!
            curCalendar.moveToNext()
            val calID: Long = curCalendar.getLong(CALENDAR_PROJECTION_ID_INDEX)

            val uriEvents: Uri = CalendarContract.Events.CONTENT_URI
            val selectionEvents: String = "((${CalendarContract.Events.CALENDAR_ID} = ?) AND (${CalendarContract.Events.DTEND} > ${Date().time}))"
            val selectionArgsEvents: Array<String> = arrayOf(calID.toString())
            val curEvents: Cursor =
                contentResolver.query(uriEvents, EVENT_PROJECTION, selectionEvents, selectionArgsEvents, null)!!
            while(curEvents.moveToNext()){
                Log.d("ev", curEvents.getString(EVENT_PROJECTION_TITLE_INDEX))
            }


        }

        if(!EasyPermissions.hasPermissions(this, Manifest.permission.READ_CALENDAR)){
            EasyPermissions.requestPermissions(this, "", 123, Manifest.permission.READ_CALENDAR)
        }

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