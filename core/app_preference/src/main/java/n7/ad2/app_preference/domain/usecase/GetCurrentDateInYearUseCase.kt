package n7.ad2.app_preference.domain.usecase

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GetCurrentDayUseCase @Inject constructor(
    private val calendar: Calendar,
) {

    operator fun invoke(
        date: Date = calendar.time,
        locale: Locale = Locale.getDefault(),
    ): Int {
        val currentDayInString = SimpleDateFormat("DDD", locale).format(date)
        return currentDayInString.toInt()
    }

}