package n7.ad2.games.internal.games.mid

import kotlin.random.Random

/** How well the bot plays: how many creeps it lets go, how far its hits land from the moment and how often it denies */
internal enum class BotLevel(val skipChance: Float, val spreadMs: Int, val denyChance: Float) {
    EASY(skipChance = 0.45f, spreadMs = 320, denyChance = 0.1f),
    MEDIUM(skipChance = 0.2f, spreadMs = 160, denyChance = 0.4f),
    PRO(skipChance = 0.06f, spreadMs = 60, denyChance = 0.75f),
}

internal data class PlannedAttack(val creepId: Int, val startAt: Long, val landAt: Long)

private const val AIM_DELAY_MS = 60

/**
 * The bot's attacks, planned in advance from [seed] like the lane itself, so a game with a bot is the same on both
 * phones. It aims a little after a creep becomes killable, misses by up to [BotLevel.spreadMs] and does not swing
 * again before its attack time has passed.
 */
internal fun planBot(sim: LaneSim, side: Side, hero: MidHero, level: BotLevel, seed: Long): List<PlannedAttack> {
    val random = Random(seed * 31 + level.ordinal)
    val heroPoint = LaneSim.heroPoint(side)
    val wishes = sim.creeps.mapNotNull { creep ->
        val killable = sim.killableFrom(creep, hero.damage) ?: return@mapNotNull null
        val skip = if (creep.side == side) 1f - level.denyChance else level.skipChance
        if (random.nextFloat() < skip) return@mapNotNull null
        val aim = killable + AIM_DELAY_MS + random.nextInt(-level.spreadMs, level.spreadMs + 1)
        val start = aim - hero.attackPointMs - hero.flightMs(LaneSim.distance(heroPoint, creep.slot))
        PlannedAttack(creep.id, start, aim)
    }.sortedBy { it.startAt }
    val plan = mutableListOf<PlannedAttack>()
    wishes.forEach { wish ->
        if (plan.isEmpty() || wish.startAt - plan.last().startAt >= hero.attackRateMs) plan += wish
    }
    return plan
}
