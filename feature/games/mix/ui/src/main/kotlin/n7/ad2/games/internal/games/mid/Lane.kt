package n7.ad2.games.internal.games.mid

import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

/** Side of the lane: Radiant comes from the bottom left, Dire from the top right. The host plays Radiant. */
internal enum class Side {
    RADIANT,
    DIRE,
    ;

    val enemy: Side get() = if (this == RADIANT) DIRE else RADIANT
}

/** [heroDamage] - share of a hero's hit the creep takes: siege creeps are armored against heroes. */
internal enum class CreepType(val hp: Int, val gold: Int, val size: Float, val heroDamage: Float) {
    MELEE(hp = 550, gold = 37, size = 1f, heroDamage = 1f),
    RANGED(hp = 300, gold = 46, size = 0.9f, heroDamage = 1f),
    SIEGE(hp = 800, gold = 66, size = 1.25f, heroDamage = 0.7f),
}

/** A place on the lane: [t] 0 is the Radiant base and 1 the Dire one, [aside] is a step across the lane, -1..1. */
internal data class LanePoint(val t: Float, val aside: Float)

internal data class Hit(val at: Long, val damage: Int)

/**
 * A creep known in full in advance: it walks from its base to [slot], stands there from [arriveAt], takes [hits] from
 * the enemy creeps and is killed by them at [deathAt], unless a hero takes it first.
 */
internal data class Creep(
    val id: Int,
    val side: Side,
    val type: CreepType,
    val wave: Int,
    val spawnAt: Long,
    val arriveAt: Long,
    val slot: LanePoint,
    val hits: List<Hit>,
    val deathAt: Long,
)

/**
 * The mid lane: waves of creeps from both bases meet at the river and fight. Everything comes from [seed], so both
 * phones of a network game see the same lane and only the heroes' hits travel over the network.
 * Before a creep dies its health stays low for a moment: that is the window for a last hit or a deny.
 */
internal class LaneSim(seed: Long) {

    val creeps: List<Creep> = generate(Random(seed))

    val endsAt: Long = creeps.maxOf { it.deathAt } + END_PAUSE_MS

    fun creep(id: Int): Creep = creeps[id]

    fun isAlive(creep: Creep, time: Long): Boolean = time >= creep.spawnAt && time < creep.deathAt

    fun hpAt(creep: Creep, time: Long): Int = creep.type.hp - creep.hits.filter { it.at <= time }.sumOf { it.damage }

    fun positionAt(creep: Creep, time: Long): LanePoint {
        if (time >= creep.arriveAt) return creep.slot
        val base = baseT(creep.side)
        if (time <= creep.spawnAt) return LanePoint(base, creep.slot.aside)
        val walked = (time - creep.spawnAt).toFloat() / (creep.arriveAt - creep.spawnAt)
        return LanePoint(base + (creep.slot.t - base) * walked, creep.slot.aside)
    }

    /** First moment the creep's health is low enough for a hit of [damage] (its armor counted), null - never */
    fun killableFrom(creep: Creep, damage: Int): Long? {
        val effective = (damage * creep.type.heroDamage).toInt()
        var hp = creep.type.hp
        if (hp <= effective) return creep.spawnAt
        creep.hits.forEach { hit ->
            hp -= hit.damage
            if (hp <= effective) return hit.at
        }
        return null
    }

    private fun generate(random: Random): List<Creep> {
        val result = mutableListOf<Creep>()
        for (wave in 0 until WAVES) {
            val types = listOf(CreepType.MELEE, CreepType.MELEE, CreepType.MELEE, CreepType.RANGED) +
                if (wave in SIEGE_WAVES) listOf(CreepType.SIEGE) else emptyList()
            val slots = slots(types)
            for (side in Side.entries) {
                types.forEachIndexed { index, type ->
                    val ownSlot = slots[index]
                    val slot = if (side == Side.RADIANT) ownSlot else LanePoint(1f - ownSlot.t, ownSlot.aside)
                    result += creep(random, id = result.size, side, type, wave, index, slot)
                }
            }
        }
        return result
    }

    /** Melee creeps in the front row, the ranged one and the siege behind them; for Radiant, Dire mirrors them */
    private fun slots(types: List<CreepType>): List<LanePoint> {
        var melee = 0
        return types.map { type ->
            when (type) {
                CreepType.MELEE -> LanePoint(MELEE_T, MELEE_ASIDES[melee++])
                CreepType.RANGED -> LanePoint(RANGED_T, RANGED_ASIDE)
                CreepType.SIEGE -> LanePoint(SIEGE_T, SIEGE_ASIDE)
            }
        }
    }

    private fun creep(random: Random, id: Int, side: Side, type: CreepType, wave: Int, index: Int, slot: LanePoint): Creep {
        val spawnAt = wave * WAVE_INTERVAL_MS + index * SPAWN_STAGGER_MS
        val speed = if (type == CreepType.SIEGE) SIEGE_SPEED else SPEED
        val arriveAt = spawnAt + (kotlin.math.abs(slot.t - baseT(side)) / speed * 1000).toLong()
        val life = when (type) {
            CreepType.MELEE -> 6_000L + random.nextLong(9_000)
            CreepType.RANGED -> 5_000L + random.nextLong(6_000)
            CreepType.SIEGE -> 11_000L + random.nextLong(7_000)
        }
        // the window gets shorter with every wave
        val window = max(WINDOW_MIN_MS, WINDOW_FIRST_MS - WINDOW_STEP_MS * wave) + random.nextLong(WINDOW_EXTRA_MS)
        val deathAt = arriveAt + life
        val lowAt = deathAt - window
        val times = mutableListOf<Long>()
        var at = arriveAt + FIRST_HIT_MS + random.nextLong(FIRST_HIT_EXTRA_MS)
        while (at < lowAt) {
            times += at
            at += HIT_INTERVAL_MS + random.nextLong(HIT_INTERVAL_EXTRA_MS)
        }
        times += lowAt
        // health left for the window: below the weakest hero's hit, even through the siege armor
        val lowest = (LOW_HP * type.heroDamage).toInt()
        val target = MIN_LEFT_HP + random.nextInt(max(1, lowest - MIN_LEFT_HP))
        val hits = spread(random, total = type.hp - target, times)
        return Creep(id, side, type, wave, spawnAt, arriveAt, slot, hits, deathAt)
    }

    /** Splits [total] damage over [times] unevenly, every hit at least 1 */
    private fun spread(random: Random, total: Int, times: List<Long>): List<Hit> {
        val weights = times.map { 0.7 + random.nextDouble() * 0.6 }
        val sum = weights.sum()
        var left = total
        return times.mapIndexed { index, time ->
            val hitsAfter = times.lastIndex - index
            val damage = if (hitsAfter == 0) left else (total * weights[index] / sum).roundToInt().coerceIn(1, left - hitsAfter)
            left -= damage
            Hit(time, damage)
        }
    }

    companion object {
        const val WAVES = 5
        private val SIEGE_WAVES = setOf(2, 4)
        private const val WAVE_INTERVAL_MS = 26_000L
        private const val SPAWN_STAGGER_MS = 250L
        private const val SPEED = 0.09f
        private const val SIEGE_SPEED = 0.07f
        private const val MELEE_T = 0.45f
        private val MELEE_ASIDES = listOf(-0.55f, 0f, 0.55f)
        private const val RANGED_T = 0.39f
        private const val RANGED_ASIDE = 0.3f
        private const val SIEGE_T = 0.35f
        private const val SIEGE_ASIDE = -0.3f
        private const val FIRST_HIT_MS = 400L
        private const val FIRST_HIT_EXTRA_MS = 500L
        private const val HIT_INTERVAL_MS = 600L
        private const val HIT_INTERVAL_EXTRA_MS = 400L
        private const val WINDOW_FIRST_MS = 2_200L
        private const val WINDOW_STEP_MS = 250L
        private const val WINDOW_MIN_MS = 1_000L
        private const val WINDOW_EXTRA_MS = 600L
        private const val LOW_HP = 36
        private const val MIN_LEFT_HP = 8
        private const val END_PAUSE_MS = 1_500L

        /** Lane length and half width in game units, for the flight time of a projectile */
        private const val LANE_UNITS = 1_400f
        private const val HALF_WIDTH_UNITS = 120f

        private const val HERO_T = 0.3f

        fun baseT(side: Side): Float = if (side == Side.RADIANT) 0f else 1f

        /** Heroes stand behind their creeps */
        fun heroPoint(side: Side): LanePoint = LanePoint(if (side == Side.RADIANT) HERO_T else 1f - HERO_T, 0f)

        fun distance(a: LanePoint, b: LanePoint): Float = hypot((a.t - b.t) * LANE_UNITS, (a.aside - b.aside) * HALF_WIDTH_UNITS)
    }
}
