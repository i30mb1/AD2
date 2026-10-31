package n7.ad2.games.internal.games.show

import kotlin.math.roundToInt
import kotlin.random.Random

/** What a round asks */
internal enum class ShowKind { MANA, HERO, VOICE, COMPARE, BUILD }

/** A round of the show: [final] - the last one, its answers open only after «Я первый» */
internal data class ShowRound(val index: Int, val kind: ShowKind, val final: Boolean)

internal object ShowPlan {
    const val ROUNDS = 6

    /** The final hides the answers until the buzz, so only kinds with four answers fit */
    val FINAL_KINDS = listOf(ShowKind.MANA, ShowKind.HERO, ShowKind.VOICE)

    /** Every kind once, then the final of another kind than the round before it; the same [seed] - the same show */
    fun create(seed: Long): List<ShowRound> {
        val random = Random(seed)
        val kinds = ShowKind.entries.shuffled(random)
        val final = FINAL_KINDS.filter { it != kinds.last() }.random(random)
        return (kinds + final).mapIndexed { index, kind -> ShowRound(index, kind, final = index == ROUNDS - 1) }
    }
}

/** The fuse of a round: burns [lengthMs] from [startedAt], a freeze stops it for [FREEZE_MS], once */
internal data class Fuse(val startedAt: Long, val lengthMs: Long, val frozenAt: Long? = null) {

    fun left(now: Long): Long {
        val frozen = frozenAt?.let { (now - it).coerceIn(0, FREEZE_MS) } ?: 0
        return (lengthMs - (now - startedAt - frozen)).coerceIn(0, lengthMs)
    }

    fun share(now: Long): Float = left(now).toFloat() / lengthMs

    fun isFrozen(now: Long): Boolean = frozenAt != null && now - frozenAt in 0 until FREEZE_MS

    fun freeze(now: Long): Fuse = if (frozenAt != null) this else copy(frozenAt = now)

    companion object {
        const val FREEZE_MS = 5_000L
    }
}

internal object ShowScoring {
    private const val BASE = 100
    private const val SPEED = 100
    private const val BET_LOSS = 100
    private const val FINAL_BASE = 200
    private const val FINAL_SPEED = 200
    private const val FINAL_MISS = 100

    /** [share] - how much of the fuse was left at the answer */
    fun round(correct: Boolean, share: Float, bet: Boolean): Int = when {
        correct -> (BASE + (SPEED * share.coerceIn(0f, 1f)).roundToInt()) * if (bet) 2 else 1
        bet -> -BET_LOSS
        else -> 0
    }

    /** [buzzShare] - how much of the fuse was left at «Я первый», null - did not press it */
    fun final(correct: Boolean, buzzShare: Float?): Int = when {
        buzzShare == null -> 0
        correct -> FINAL_BASE + (FINAL_SPEED * buzzShare.coerceIn(0f, 1f)).roundToInt()
        else -> -FINAL_MISS
    }
}

/** 50 на 50: two wrong answers to hide, the right one stays */
internal fun fiftyFifty(rightIndex: Int, count: Int, random: Random): Set<Int> =
    (0 until count).filter { it != rightIndex }.shuffled(random).take(2).toSet()
