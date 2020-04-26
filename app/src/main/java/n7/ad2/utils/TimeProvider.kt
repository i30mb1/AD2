package n7.ad2.utils

import java.util.*
import java.util.concurrent.*


fun nowInMillis(): Long = System.currentTimeMillis()

fun nowInSeconds(): Long = TimeUnit.MILLISECONDS.toSeconds(nowInMillis())

fun now(): Date = toDate(nowInSeconds())

fun toDate(seconds: Long): Date {
    val millis = seconds * 1000
    return Date(millis)
}

fun isSameDay(date1: Date, date2: Date): Boolean {
    return isSameDay(
            Calendar.getInstance().apply { time = date1 },
            Calendar.getInstance().apply { time = date2 }
    )
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}