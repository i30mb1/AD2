package n7.ad2.games.internal.games.invoker

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

internal enum class Orb(val key: String, val title: String, private val image: String) {
    QUAS("Q", "Quas", "invoker_quas"),
    WEX("W", "Wex", "invoker_wex"),
    EXORT("E", "Exort", "invoker_exort"),
    ;

    val imageUrl: String get() = abilityImage(image)
}

/** Invoker's spells: each is three orbs, the order does not matter */
internal enum class InvokerSpell(val title: String, private val image: String, val orbs: List<Orb>) {
    COLD_SNAP("Cold Snap", "invoker_cold_snap", listOf(Orb.QUAS, Orb.QUAS, Orb.QUAS)),
    GHOST_WALK("Ghost Walk", "invoker_ghost_walk", listOf(Orb.QUAS, Orb.QUAS, Orb.WEX)),
    ICE_WALL("Ice Wall", "invoker_ice_wall", listOf(Orb.QUAS, Orb.QUAS, Orb.EXORT)),
    EMP("E.M.P.", "invoker_emp", listOf(Orb.WEX, Orb.WEX, Orb.WEX)),
    TORNADO("Tornado", "invoker_tornado", listOf(Orb.WEX, Orb.WEX, Orb.QUAS)),
    ALACRITY("Alacrity", "invoker_alacrity", listOf(Orb.WEX, Orb.WEX, Orb.EXORT)),
    SUN_STRIKE("Sun Strike", "invoker_sun_strike", listOf(Orb.EXORT, Orb.EXORT, Orb.EXORT)),
    FORGE_SPIRIT("Forge Spirit", "invoker_forge_spirit", listOf(Orb.EXORT, Orb.EXORT, Orb.QUAS)),
    CHAOS_METEOR("Chaos Meteor", "invoker_chaos_meteor", listOf(Orb.EXORT, Orb.EXORT, Orb.WEX)),
    DEAFENING_BLAST("Deafening Blast", "invoker_deafening_blast", listOf(Orb.QUAS, Orb.WEX, Orb.EXORT)),
    ;

    val imageUrl: String get() = abilityImage(image)

    companion object {
        fun of(orbs: List<Orb>): InvokerSpell? = if (orbs.size != ORBS) null else entries.firstOrNull { it.orbs.sorted() == orbs.sorted() }
    }
}

private const val ORBS = 3
private const val QUEUE = 300

/** 100 for a spell and 10 more for each spell of the streak, up to double; the open hint takes half */
internal object InvokerScoring {
    private const val BASE = 100
    private const val STREAK_STEP = 10
    private const val STREAK_CAP = 10

    fun points(streak: Int, hinted: Boolean): Int {
        val full = BASE + STREAK_STEP * min(streak, STREAK_CAP)
        return if (hinted) full / 2 else full
    }
}

internal const val INVOKE_IMAGE = "file:///android_asset/images/abilities/invoker_invoke.webp"

private fun abilityImage(name: String) = "file:///android_asset/images/abilities/$name.webp"

/** A new orb pushes out the oldest: there are three at most */
internal fun List<Orb>.push(orb: Orb): List<Orb> = (this + orb).takeLast(ORBS)

/** What is off with the orbs for a spell: the orbs to add and the positions of the ones that do not belong */
internal data class OrbsDiff(val missing: List<Orb>, val extra: Set<Int>)

internal fun diff(orbs: List<Orb>, spell: InvokerSpell): OrbsDiff {
    val missing = spell.orbs.toMutableList()
    val extra = orbs.indices.filterNot { missing.remove(orbs[it]) }.toSet()
    return OrbsDiff(missing, extra)
}

/** The spells to invoke one after another, never the same twice in a row; the same seed - the same queue on every phone */
internal fun spellQueue(seed: Long): List<InvokerSpell> {
    val random = Random(seed)
    val queue = mutableListOf<InvokerSpell>()
    while (queue.size < QUEUE) {
        val spell = InvokerSpell.entries.random(random)
        if (spell != queue.lastOrNull()) queue += spell
    }
    return queue
}

/**
 * A run of invokes. [target] is the spell to invoke, [orbs] - the orbs now, [wrong] - what was off at the last wrong invoke,
 * [hinted] - the recipe of the target is open, the spell gives half the points.
 */
internal data class InvokerRun(
    val queue: List<InvokerSpell>,
    val index: Int = 0,
    val orbs: List<Orb> = emptyList(),
    val score: Int = 0,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val invoked: Int = 0,
    val mistakes: Int = 0,
    val hinted: Boolean = false,
    val wrong: OrbsDiff? = null,
    val lastPoints: Int? = null,
) {
    val target: InvokerSpell get() = queue[index % queue.size]

    /** The spells after the target */
    val next: List<InvokerSpell> get() = (1..NEXT).map { queue[(index + it) % queue.size] }

    fun press(orb: Orb): InvokerRun = copy(orbs = orbs.push(orb), wrong = null)

    /** As in the game the orbs stay after invoking */
    fun invoke(): InvokerRun {
        if (InvokerSpell.of(orbs) != target) return copy(streak = 0, mistakes = mistakes + 1, wrong = diff(orbs, target), lastPoints = null)
        val points = InvokerScoring.points(streak, hinted)
        return copy(
            index = index + 1,
            score = score + points,
            streak = streak + 1,
            bestStreak = max(bestStreak, streak + 1),
            invoked = invoked + 1,
            hinted = false,
            wrong = null,
            lastPoints = points,
        )
    }

    fun hint(): InvokerRun = copy(hinted = true)

    private companion object {
        const val NEXT = 4
    }
}

/** My score and how many spells, "s;score;invoked" */
internal object InvokerCodec {

    fun encode(score: Int, invoked: Int): String = "$SCORE;$score;$invoked"

    fun decode(payload: String): Pair<Int, Int>? {
        val parts = payload.split(";")
        if (parts.size != 3 || parts[0] != SCORE) return null
        val score = parts[1].toIntOrNull() ?: return null
        val invoked = parts[2].toIntOrNull() ?: return null
        return score to invoked
    }

    private const val SCORE = "s"
}
