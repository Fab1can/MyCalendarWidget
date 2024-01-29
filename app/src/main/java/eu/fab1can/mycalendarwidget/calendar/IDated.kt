package eu.fab1can.mycalendarwidget.calendar

import java.time.LocalDateTime

interface IDated {
    fun currentYearDateTime():LocalDateTime
}