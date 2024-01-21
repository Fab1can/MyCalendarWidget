package eu.fab1can.mycalendarwidget.calendar

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import java.util.Date

class Event (id: Long, calendarId: Long, title: String, dtEnd: Long){
    val ID = id
    val CalendarID = calendarId
    val Title = title
    val DtEnd = dtEnd

    companion object{
        private val PROJECTION: Array<String> = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTEND
        )
        private val PROJECTION_ID_INDEX: Int = 0
        private val PROJECTION_TITLE_INDEX: Int = 1
        private val PROJECTION_DTEND_INDEX: Int = 2
        fun retrieveEvents(selection:String, selectionArgs: Array<String>, contentResolver: ContentResolver, calID: Long): Array<Event> {
            val uri: Uri = CalendarContract.Events.CONTENT_URI
            val cur: Cursor =
                contentResolver.query(uri, PROJECTION, selection, selectionArgs, null)!!
            val entrees = mutableListOf<Event>()
            while(cur.moveToNext()){
                val id = cur.getLong(PROJECTION_ID_INDEX)
                val title = cur.getString(PROJECTION_TITLE_INDEX)
                val dtEnd = cur.getLong(PROJECTION_DTEND_INDEX)
                entrees.add(Event(id, calID, title, dtEnd))
            }
            return entrees.toTypedArray()
        }

        fun retrieveEvents(selection:String, selectionArgs: Array<String>, contentResolver: ContentResolver, calIDs: Array<Long>): Array<Event> {
            val uri: Uri = CalendarContract.Events.CONTENT_URI
            val cur: Cursor =
                contentResolver.query(uri, PROJECTION, selection, selectionArgs, null)!!
            val entrees = mutableListOf<Event>()
            while(cur.moveToNext()){
                val id = cur.getLong(PROJECTION_ID_INDEX)
                val title = cur.getString(PROJECTION_TITLE_INDEX)
                val dtEnd = cur.getLong(PROJECTION_DTEND_INDEX)
                entrees.add(Event(id, calID, title, dtEnd))
            }
            return entrees.toTypedArray()
        }
    }
}