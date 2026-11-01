package n7.ad2.games.internal.progress

import android.app.Application
import android.content.Context
import androidx.compose.runtime.Immutable
import javax.inject.Inject

/**
 * The player's experience: every finished game gives points, a level needs [needed] of them, each next one 50 more.
 * [inLevel] - the points gathered on the current level.
 */
@Immutable
internal data class Progress(val xp: Int) {

    val level: Int
    val inLevel: Int
    val needed: Int

    init {
        var level = 1
        var left = xp
        while (left >= needed(level)) {
            left -= needed(level)
            level++
        }
        this.level = level
        inLevel = left
        needed = needed(level)
    }

    val fraction: Float get() = inLevel.toFloat() / needed

    operator fun plus(points: Int) = Progress(xp + points)

    /** How many levels up [after] is */
    fun levelsUpTo(after: Progress): Int = after.level - level

    companion object {
        fun needed(level: Int): Int = 150 + 50 * level
    }
}

/** How many points a game gives */
internal object GameXp {
    private const val FINISHED = 30
    private const val BONUS = 20

    /** A finished game; a win or a new record gives more */
    fun game(won: Boolean, record: Boolean): Int = FINISHED + if (won || record) BONUS else 0

    const val DAILY = 50
}

/** The experience of the player on this phone */
internal class ProgressStore @Inject constructor(application: Application) {

    private val preferences = application.getSharedPreferences("games_progress", Context.MODE_PRIVATE)

    fun progress(): Progress = Progress(preferences.getInt(KEY_XP, 0))

    /** Adds the points of a game; the result tells the points and the levels gained, for the end screen */
    fun add(points: Int): XpGain {
        val before = progress()
        val after = before + points
        preferences.edit().putInt(KEY_XP, after.xp).apply()
        return XpGain(points, after.level.takeIf { before.levelsUpTo(after) > 0 })
    }

    private companion object {
        const val KEY_XP = "xp"
    }
}

/** What a game gave: the points and the new level, null - the same level */
@Immutable
internal data class XpGain(val points: Int, val newLevel: Int?)
