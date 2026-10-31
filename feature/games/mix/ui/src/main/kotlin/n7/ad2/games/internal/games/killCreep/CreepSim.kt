package n7.ad2.games.internal.games.killCreep

import kotlin.math.hypot
import kotlin.random.Random

/**
 * [lastHitDamage] - урон игрока по этому типу: у осадного меньше (броня), поэтому черта на полоске HP у него ниже.
 * [speed] - доля поля в секунду, [size] - множитель размера (и радиуса попадания)
 */
internal enum class CreepType(val hp: Int, val gold: Int, val speed: Float, val size: Float, val lastHitDamage: Int, val emoji: String) {
    MELEE(hp = 550, gold = 38, speed = 0.16f, size = 1f, lastHitDamage = 60, emoji = "👹"),
    RANGED(hp = 300, gold = 45, speed = 0.26f, size = 0.9f, lastHitDamage = 60, emoji = "🏹"),
    SIEGE(hp = 900, gold = 70, speed = 0.09f, size = 1.3f, lastHitDamage = 36, emoji = "🛞"),
    FLAG(hp = 700, gold = 100, speed = 0.14f, size = 1.1f, lastHitDamage = 60, emoji = "🚩"),
    MEGA(hp = 1200, gold = 200, speed = 0.22f, size = 1.4f, lastHitDamage = 60, emoji = "👑"),
}

/**
 * Крип целиком задан заранее: путь [waypoints] (обходит их со скоростью типа), удары союзников [allyHits],
 * момент [killableAt], когда HP опускается до урона игрока, и [deathAt], когда его добивают союзники
 */
internal data class Creep(
    val id: Int,
    val type: CreepType,
    val spawnAt: Long,
    val waypoints: List<Pair<Float, Float>>,
    val allyHits: List<Pair<Long, Int>>,
    val killableAt: Long,
    val deathAt: Long,
)

/** Крип на поле в момент времени: координаты 0..1, текущее HP */
internal data class VisibleCreep(val id: Int, val type: CreepType, val x: Float, val y: Float, val hp: Int) {
    val isKillable: Boolean get() = hp <= type.lastHitDamage
}

/**
 * Детерминированное поле Kill Creep: всё (волны, пути, урон союзников) выводится из [seed],
 * поэтому в сетевой игре оба телефона видят одно и то же, передавать позиции не нужно - только тапы.
 */
internal class CreepSim(seed: Long) {

    val creeps: List<Creep> = generate(Random(seed))

    fun visibleAt(timeMs: Long): List<VisibleCreep> = creeps
        .filter { timeMs >= it.spawnAt && timeMs < it.deathAt }
        .map { creep ->
            val (x, y) = positionAt(creep, timeMs)
            VisibleCreep(creep.id, creep.type, x, y, hpAt(creep, timeMs))
        }

    fun hpAt(creep: Creep, timeMs: Long): Int = creep.type.hp - creep.allyHits.filter { (at, _) -> at <= timeMs }.sumOf { it.second }

    /** Ближайший к точке тапа крип в радиусе попадания (с учетом размера), null - тап мимо */
    fun hitTest(timeMs: Long, x: Float, y: Float, aspect: Float): VisibleCreep? = visibleAt(timeMs)
        .map { it to hypot(it.x - x, (it.y - y) / aspect) }
        .filter { (creep, distance) -> distance <= HIT_RADIUS * creep.type.size }
        .minByOrNull { it.second }
        ?.first

    private fun positionAt(creep: Creep, timeMs: Long): Pair<Float, Float> {
        var left = (timeMs - creep.spawnAt) / 1000f * creep.type.speed
        val points = creep.waypoints
        for (index in 0 until points.lastIndex) {
            val (x1, y1) = points[index]
            val (x2, y2) = points[index + 1]
            val length = hypot(x2 - x1, y2 - y1)
            if (left <= length || index == points.lastIndex - 1) {
                val fraction = if (length == 0f) 1f else (left / length).coerceIn(0f, 1f)
                return x1 + (x2 - x1) * fraction to y1 + (y2 - y1) * fraction
            }
            left -= length
        }
        return points.last()
    }

    private fun generate(random: Random): List<Creep> {
        val result = mutableListOf<Creep>()
        var time = 0L
        var id = 0
        val megaAt = 30_000L + random.nextLong(10_000)
        var megaSpawned = false
        // стартовая волна из 3 крипов, дальше интервал сокращается с ~2.2 до ~0.8 с
        repeat(START_WAVE) { result += creep(random, id++, time + it * 300L, pickType(random), 0f) }
        while (true) {
            val progress = time / DURATION_MS.toFloat()
            time += (2_200 - 1_400 * progress).toLong() + random.nextLong(400)
            if (time >= DURATION_MS - 2_000) break
            val type = if (!megaSpawned && time >= megaAt) CreepType.MEGA.also { megaSpawned = true } else pickType(random)
            result += creep(random, id++, time, type, time / DURATION_MS.toFloat())
        }
        return result
    }

    private fun pickType(random: Random): CreepType = when (random.nextInt(100)) {
        in 0 until 45 -> CreepType.MELEE
        in 45 until 78 -> CreepType.RANGED
        in 78 until 92 -> CreepType.SIEGE
        else -> CreepType.FLAG
    }

    private fun creep(random: Random, id: Int, spawnAt: Long, type: CreepType, progress: Float): Creep {
        // выходит с края поля и бежит через случайные точки
        val start = when (random.nextInt(4)) {
            0 -> random.nextFloat() to 0f
            1 -> 1f to random.nextFloat()
            2 -> random.nextFloat() to 1f
            else -> 0f to random.nextFloat()
        }
        val waypoints = listOf(start) + List(WAYPOINTS) { MARGIN + random.nextFloat() * (1 - 2 * MARGIN) to MARGIN + random.nextFloat() * (1 - 2 * MARGIN) }
        var hp = type.hp
        var at = spawnAt + 600 + random.nextLong(600)
        val hits = mutableListOf<Pair<Long, Int>>()
        val minHit = if (type == CreepType.SIEGE) 25 else 40
        while (hp > type.lastHitDamage) {
            val damage = (minHit + random.nextInt(45)).coerceAtMost(hp - 1).coerceAtLeast(1)
            hits += at to damage
            hp -= damage
            at += 450 + random.nextLong(500)
        }
        val killableAt = hits.last().first
        // окно на добивание сужается к концу игры: 1.6-2.6 с в начале, 0.9-1.7 с в конце
        val window = (1_600 - 700 * progress).toLong() + random.nextLong(1_000)
        val deathAt = killableAt + window
        return Creep(id, type, spawnAt, waypoints, hits + (deathAt to hp), killableAt, deathAt)
    }

    companion object {
        const val DURATION_MS = 60_000L
        private const val START_WAVE = 3
        private const val WAYPOINTS = 12
        private const val MARGIN = 0.08f
        private const val HIT_RADIUS = 0.09f
    }
}
