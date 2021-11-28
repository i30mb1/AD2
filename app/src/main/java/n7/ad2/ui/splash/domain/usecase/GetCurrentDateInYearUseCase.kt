package n7.ad2.ui.splash.domain.usecase

import ad2.n7.coroutines.DispatchersProvider
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GetCurrentDateInYearUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val calendar: Calendar,
) {

    suspend operator fun invoke(
        date: Date = calendar.time,
        locale: Locale = Locale.getDefault(),
    ) = withContext(dispatchers.IO) {
        val currentDayInString = SimpleDateFormat("DDD", locale).format(date)
        currentDayInString.toInt()
    }

}