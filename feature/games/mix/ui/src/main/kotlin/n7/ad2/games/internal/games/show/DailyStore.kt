package n7.ad2.games.internal.games.show

import android.app.Application
import android.content.Context
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

/** The result of today's challenge and the streak, on this phone */
internal class DailyStore @Inject constructor(application: Application) {

    private val preferences = application.getSharedPreferences("games_daily", Context.MODE_PRIVATE)

    /** null - not played on [date] yet */
    fun result(date: LocalDate): DailyResult? = preferences.getString(KEY_RESULT, null)?.let(DailyCodec::decode)?.takeIf { it.date == date }

    fun streak(): DailyStreak {
        val lastDay = preferences.getString(KEY_STREAK_DAY, null)?.let { day ->
            try {
                LocalDate.parse(day)
            } catch (e: DateTimeParseException) {
                null
            }
        }
        return DailyStreak(lastDay, preferences.getInt(KEY_STREAK_DAYS, 0))
    }

    /** Keeps the result of the day and moves the streak */
    fun save(result: DailyResult): DailyStreak {
        val streak = streak().played(result.date)
        preferences.edit()
            .putString(KEY_RESULT, DailyCodec.encode(result))
            .putString(KEY_STREAK_DAY, streak.lastDay.toString())
            .putInt(KEY_STREAK_DAYS, streak.days)
            .apply()
        return streak
    }

    private companion object {
        const val KEY_RESULT = "result"
        const val KEY_STREAK_DAY = "streak_day"
        const val KEY_STREAK_DAYS = "streak_days"
    }
}
