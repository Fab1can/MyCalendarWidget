package eu.fab1can.mycalendarwidget.calendar

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract

class Calendar(id: Long, accName: String) {
    val ID = id
    val AccName = accName


    companion object {
        private val PROJECTION: Array<String> = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME
        )
        private val PROJECTION_ID_INDEX: Int = 0
        private val PROJECTION_ACCOUNT_NAME_INDEX: Int = 1
        fun retreiveAll(contentResolver: ContentResolver): Array<Calendar> {
            val uri: Uri = CalendarContract.Calendars.CONTENT_URI
            val selection: String = ""
            val selectionArgsCalendar: Array<String> = arrayOf()
            val curCalendar: Cursor =
                contentResolver.query(uri, PROJECTION, selection, selectionArgsCalendar, null)!!
            val entrees = mutableListOf<Calendar>()
            while(curCalendar.moveToNext()){
                val id: Long = curCalendar.getLong(PROJECTION_ID_INDEX)
                val accName = curCalendar.getString(PROJECTION_ACCOUNT_NAME_INDEX)
                entrees.add(Calendar(id, accName))
            }
            return entrees.toTypedArray()
        }
    }
}