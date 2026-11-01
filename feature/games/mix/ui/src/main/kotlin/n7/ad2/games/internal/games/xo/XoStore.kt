package n7.ad2.games.internal.games.xo

import android.app.Application
import android.content.Context
import javax.inject.Inject
import n7.ad2.games.room.GameMode

/** Wins in a row on this phone, against the bot and against friends apart */
internal class XoStore @Inject constructor(application: Application) {

    private val preferences = application.getSharedPreferences("games_xo", Context.MODE_PRIVATE)

    fun streak(mode: GameMode): XoStreak = XoStreak(preferences.getInt(key(mode), 0))

    fun save(mode: GameMode, streak: XoStreak) {
        preferences.edit().putInt(key(mode), streak.current).apply()
    }

    private fun key(mode: GameMode) = "streak_${mode.name}"
}
