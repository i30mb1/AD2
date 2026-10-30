package n7.ad2.news.ui.internal.common

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** "7 окт" for this year, "7 окт 2025" for older news; empty when the date is unknown. */
internal fun formatShortNewsDate(seconds: Long): String {
    if (seconds <= 0) return ""
    val date = Instant.ofEpochSecond(seconds).atZone(ZoneId.systemDefault()).toLocalDate()
    val pattern = if (date.year == LocalDate.now().year) "d MMM" else "d MMM yyyy"
    return date.format(DateTimeFormatter.ofPattern(pattern)).replace(".", "")
}

/** "15 сентября 2026" for the article header; empty when the date is unknown. */
internal fun formatLongNewsDate(seconds: Long): String {
    if (seconds <= 0) return ""
    return Instant.ofEpochSecond(seconds).atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
}
