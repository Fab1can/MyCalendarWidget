package eu.fab1can.mycalendarwidget.calendar

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Component
import net.fortuna.ical4j.model.DateTime
import net.fortuna.ical4j.model.Period
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.util.Calendars
import net.fortuna.ical4j.util.CompatibilityHints
import java.io.StringReader
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date


class Event (id: Long, calendarId: Long, title: String, dtEnd: Long, dtStart: Long){
    val ID = id
    val CalendarID = calendarId
    val Title = title
    var DtEnd = LocalDateTime.ofInstant(Instant.ofEpochMilli(dtEnd), ZoneId.systemDefault())
    var DtStart = LocalDateTime.ofInstant(Instant.ofEpochMilli(dtStart), ZoneId.systemDefault())

    fun getNextOccurrence(dtStart: LocalDateTime, duration:String, rrule:String, title:String) {
        System.out.println(title)
        val icalString = "BEGIN:VCALENDAR\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART:"+dtStart.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"))+"\n" +  // data e ora di inizio
                "DURATION:"+duration+"\n" +    // data e ora di fine
                "RRULE:"+rrule+"\n" + // regola di ricorrenza (ogni giorno per 5 volte)
                "SUMMARY:Prossima Ricorrenza\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR"
        val calendarBuilder = CalendarBuilder()
        val calendar = calendarBuilder.build(StringReader(icalString))
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true)
        val event = calendar.getComponents<VEvent>(Component.VEVENT).first()

        val period = Period(
            DateTime(event.startDate.date),
            DateTime(event.endDate.date)
        )


        for (o in calendar.getComponents<VEvent>("VEVENT")) {
            val c = o as Component
            val list = c.calculateRecurrenceSet(period)
            for (po in list) {
                System.out.println(po as Period)
            }
        }

    }

    constructor(id: Long, calendarId: Long, title: String, dtEnd: Long, dtStart: Long, rrule: String, duration: String):this(id, calendarId, title, dtEnd, dtStart){

        getNextOccurrence(DtStart, duration, rrule, title)

        var freq : String
        var until : String
        var byday : String
        var bymonthday : String
        var byyearday : String
        for (item in rrule.split(";")){
            val item2 = item.split("=")
            if(item2[0]=="FREQ"){
                when{
                    item2[1]=="DAILY" ->{
                        val now = LocalDateTime.now()
                        DtStart = LocalDateTime.ofInstant(Instant.ofEpochMilli(dtStart), ZoneId.systemDefault()).withYear(now.year).withMonth(now.monthValue).withDayOfMonth(now.dayOfMonth)
                        if(now.isAfter(DtStart)){
                            DtStart = DtStart.plusDays(1)
                        }
                    }
                }
            }
        }
        /*if(duration.substring(duration.length-1)!="S"){
            throw Error()
        }*/
        val seconds = duration.substring(1,duration.length-1).toInt()
        DtEnd = DtStart.plusSeconds(seconds.toLong())
    }

    companion object{
        private val PROJECTION: Array<String> = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.RRULE,
            CalendarContract.Events.DURATION,
        )
        private val PROJECTION_ID_INDEX: Int = 0
        private val PROJECTION_TITLE_INDEX: Int = 1
        private val PROJECTION_DTEND_INDEX: Int = 2
        private val PROJECTION_DTSTART_INDEX: Int = 3
        private val PROJECTION_CALENDAR_ID_INDEX: Int = 4
        private val PROJECTION_RRULE_INDEX: Int = 5
        private val PROJECTION_DURATION_INDEX: Int = 6
        fun retrieveEvents(selection:String, selectionArgs: Array<String>?, contentResolver: ContentResolver): Array<Event> {
            val uri: Uri = CalendarContract.Events.CONTENT_URI
            val cur: Cursor =
                contentResolver.query(uri, PROJECTION, selection, selectionArgs, null)!!
            val entrees = mutableListOf<Event>()
            while(cur.moveToNext()){
                val id = cur.getLong(PROJECTION_ID_INDEX)
                val title = cur.getString(PROJECTION_TITLE_INDEX)
                val dtEnd = cur.getLong(PROJECTION_DTEND_INDEX)
                val dtStart = cur.getLong(PROJECTION_DTSTART_INDEX)
                val calID = cur.getLong(PROJECTION_CALENDAR_ID_INDEX)
                if(cur.isNull(PROJECTION_RRULE_INDEX)){
                    entrees.add(Event(id, calID, title, dtEnd, dtStart))
                }else{
                    val rrule = cur.getString(PROJECTION_RRULE_INDEX)
                    val duration = cur.getString(PROJECTION_DURATION_INDEX)
                    entrees.add(Event(id, calID, title, dtEnd, dtStart, rrule, duration))
                }
            }
            return entrees.toTypedArray()
        }

        fun retrieveFutureEvents(contentResolver: ContentResolver, calID: Long): Array<Event> {
            return retrieveEvents("((${CalendarContract.Events.CALENDAR_ID} = ?) AND (${CalendarContract.Events.DTEND} > ${Date().time}))",arrayOf(calID.toString()), contentResolver)
        }

        fun retrieveFutureEvents(contentResolver: ContentResolver, calIDs: Array<Long>): Array<Event> {
            return retrieveEvents("((${CalendarContract.Events.CALENDAR_ID} IN (${calIDs.joinToString()})) AND ((${CalendarContract.Events.DTEND} > ${Date().time}) OR (${CalendarContract.Events.RRULE} IS NOT NULL)))", null, contentResolver)

        }


    }
}