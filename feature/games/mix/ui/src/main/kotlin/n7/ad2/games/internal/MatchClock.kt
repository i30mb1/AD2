package n7.ad2.games.internal

/**
 * Время матча. [startedAt] берется в лобби, когда комната перешла в Playing (у обоих игроков почти одновременно),
 * а не при открытии экрана: гость открывает игру позже хоста. Отсчет [countdownMs] сглаживает остаток задержки
 */
internal class MatchClock(private val startedAt: Long, private val countdownMs: Long) {

    /** Отрицательное, пока идет отсчет */
    fun elapsed(now: Long): Long = now - startedAt - countdownMs

    /** 3, 2, 1 во время отсчета, 0 - игра идет */
    fun countdown(now: Long): Int {
        val left = -elapsed(now)
        return if (left <= 0) 0 else ((left + 999) / 1000).toInt()
    }
}
