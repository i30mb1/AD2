package n7.ad2.games.internal.games.mid

import kotlin.math.roundToLong

/**
 * A hero for the mid. [damage] is the middle of the game's damage range, [attackPointMs] the swing before the hit
 * lands, [attackRateMs] the time between attacks, [projectileSpeed] 0 for a melee hero.
 */
internal data class MidHero(
    val key: String,
    val name: String,
    val imageUrl: String,
    val damage: Int,
    val attackPointMs: Int,
    val attackRateMs: Int,
    val projectileSpeed: Int,
)

internal fun MidHero.flightMs(distance: Float): Long = if (projectileSpeed > 0) (distance / projectileSpeed * 1000).roundToLong() else 0L

/** When an attack started at [startAt] lands: after the swing a ranged hero's projectile still flies to the creep */
internal fun MidHero.landAt(startAt: Long, distance: Float): Long = startAt + attackPointMs + flightMs(distance)

internal sealed interface Outcome {
    /** The hit took the creep, unless a hit of the other hero landed earlier */
    data object Kill : Outcome

    /** The creep still had [hp] left; [byMs] - how much sooner than the creep became killable the hit landed */
    data class Early(val byMs: Long, val hp: Int) : Outcome

    /** The creep was already dead [byMs] ago */
    data class Late(val byMs: Long) : Outcome
}

internal fun LaneSim.outcome(creep: Creep, hero: MidHero, landAt: Long): Outcome {
    if (landAt >= creep.deathAt) return Outcome.Late(landAt - creep.deathAt)
    val killable = killableFrom(creep, hero.damage)
    return if (killable != null && landAt >= killable) {
        Outcome.Kill
    } else {
        Outcome.Early(byMs = (killable ?: creep.deathAt) - landAt, hp = hpAt(creep, landAt))
    }
}

/** Heroes to pick for the mid. Their swing is not in the game data, it is written here; the rest comes from the data. */
internal object MidRoster {
    val attackPointsMs: Map<String, Int> = linkedMapOf(
        "Juggernaut" to 330,
        "Sniper" to 170,
        "Shadow Fiend" to 500,
        "Lina" to 750,
    )
}
