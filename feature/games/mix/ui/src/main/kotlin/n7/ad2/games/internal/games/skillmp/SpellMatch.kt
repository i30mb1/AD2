package n7.ad2.games.internal.games.skillmp

import n7.ad2.games.room.GameMode

internal data class Answer(val correct: Boolean, val timeMs: Long)

/**
 * Правила «Угадай спелл» для всех режимов:
 * - SOLO: 3 жизни, +1 за верный ответ;
 * - VERSUS: [VERSUS_ROUNDS] раундов, верно и быстрее соперника +2, верно но медленнее (или соперник ошибся) +1;
 * - COOP: 3 общие жизни, ошибка любого игрока отнимает жизнь, счет общий (хранится в [myScore]).
 * В сетевых режимах раунд закрывается, когда ответили оба.
 */
internal data class SpellMatch(
    val mode: GameMode,
    val round: Int,
    val lives: Int,
    val myScore: Int,
    val peerScore: Int,
    val myAnswer: Answer?,
    val peerAnswer: Answer?,
    val isOver: Boolean,
    val peerLeft: Boolean,
) {

    val roundComplete: Boolean
        get() = myAnswer != null && (mode == GameMode.SOLO || peerAnswer != null)

    fun answer(mine: Boolean, answer: Answer): SpellMatch {
        if (isOver) return this
        if (mine && myAnswer != null || !mine && peerAnswer != null) return this
        val answered = if (mine) copy(myAnswer = answer) else copy(peerAnswer = answer)
        return if (answered.roundComplete) answered.score() else answered
    }

    fun nextRound(): SpellMatch = if (isOver) this else copy(round = round + 1, myAnswer = null, peerAnswer = null)

    fun peerLeft(): SpellMatch = copy(isOver = true, peerLeft = true)

    private fun score(): SpellMatch {
        val mine = requireNotNull(myAnswer)
        return when (mode) {
            GameMode.SOLO -> {
                val lives = if (mine.correct) lives else lives - 1
                copy(myScore = myScore + if (mine.correct) 1 else 0, lives = lives, isOver = lives <= 0)
            }
            GameMode.VERSUS -> {
                val peer = requireNotNull(peerAnswer)
                copy(
                    myScore = myScore + versusPoints(mine, peer),
                    peerScore = peerScore + versusPoints(peer, mine),
                    isOver = round + 1 >= VERSUS_ROUNDS,
                )
            }
            GameMode.COOP -> {
                val peer = requireNotNull(peerAnswer)
                val mistakes = listOf(mine, peer).count { !it.correct }
                val correct = 2 - mistakes
                val lives = (lives - mistakes).coerceAtLeast(0)
                copy(myScore = myScore + correct, lives = lives, isOver = lives <= 0)
            }
        }
    }

    private fun versusPoints(self: Answer, other: Answer): Int = when {
        !self.correct -> 0
        !other.correct || self.timeMs < other.timeMs -> if (other.correct) 2 else 1
        else -> 1
    }

    companion object {
        const val VERSUS_ROUNDS = 10
        private const val LIVES = 3

        fun start(mode: GameMode) = SpellMatch(mode, 0, LIVES, 0, 0, null, null, isOver = false, peerLeft = false)
    }
}
