package eu.fab1can.mycalendarwidget.calendar

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import java.time.LocalDate
import java.time.LocalDateTime

class ContactEvent:IDated {
    val Name : String
    val Label : String
    val Date : LocalDate
    val HasYear : Boolean

    constructor(name: String, label: String, date: String){
        Name = name
        Label = label
        val dateFields = date.split("-")
        if(dateFields.size==3){
            HasYear=true
            Date= LocalDate.parse(date)
        }else{
            HasYear=false
            Date = LocalDate.of(0,dateFields[2].toInt(), dateFields[3].toInt())
        }
    }

    companion object{
        private val PROJECTION: Array<String> = arrayOf(
            ContactsContract.CommonDataKinds.Event.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Event.TYPE,
            ContactsContract.CommonDataKinds.Event.LABEL,
            ContactsContract.CommonDataKinds.Event.START_DATE,
        )
        private val PROJECTION_NAME_INDEX: Int = 0
        private val PROJECTION_TYPE_INDEX: Int = 1
        private val PROJECTION_LABEL_INDEX: Int = 2
        private val PROJECTION_DATE_INDEX: Int = 3
        fun retrieveEvents(contentResolver: ContentResolver): Array<ContactEvent> {
            val selection = ContactsContract.Data.MIMETYPE + " = ? "

            val selectionArgs = arrayOf(ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)

            val uri: Uri = ContactsContract.Data.CONTENT_URI
            val cur: Cursor =
                contentResolver.query(uri, PROJECTION, selection, selectionArgs, null)!!
            val entrees = mutableListOf<ContactEvent>()
            while(cur.moveToNext()){
                val name = cur.getString(PROJECTION_NAME_INDEX)
                val type = cur.getInt(PROJECTION_TYPE_INDEX)
                val label = when(type){
                    ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY->"Birthday"
                    ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY->"Anniversary"
                    ContactsContract.CommonDataKinds.Event.TYPE_OTHER->"Other"
                    else->cur.getString(PROJECTION_LABEL_INDEX)
                }
                val date = cur.getString(PROJECTION_DATE_INDEX)
                entrees.add(ContactEvent(name, label, date))


            }
            return entrees.toTypedArray()
        }

        fun retrieveNextEvents(contentResolver: ContentResolver): Array<ContactEvent> {
            return retrieveEvents(contentResolver).filter { ev->ev.Date.withYear(LocalDate.now().year)>LocalDate.now()}.toTypedArray()
        }


    }

    override fun currentYearDateTime(): LocalDateTime {
        if(Date.withYear(LocalDate.now().year)<LocalDate.now()){
            return Date.atStartOfDay().withYear(LocalDate.now().year+1)
        }else{
            return Date.atStartOfDay().withYear(LocalDate.now().year)
        }
    }
}