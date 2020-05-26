package n7.ad2.ui.splash.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GetCurrentDateInYearUseCase @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher,
        private val calendar: Calendar
) {

    suspend operator fun invoke(date: Date = calendar.time, locale: Locale = Locale.getDefault()) = withContext(ioDispatcher) {
        val currentDayInString = SimpleDateFormat("DDD", locale).format(date)
        currentDayInString.toInt()
    }

}