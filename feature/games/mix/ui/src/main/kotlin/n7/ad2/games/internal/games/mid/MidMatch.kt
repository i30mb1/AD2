package n7.ad2.games.internal.games.mid

/** A creep taken by a hero of [side]: an own creep is a deny and gives no gold */
internal data class Claim(val side: Side, val landAt: Long, val deny: Boolean, val gold: Int)

/**
 * Score of the mid. A creep goes to the hit that landed first, whatever arrived over the network first; hits landing
 * at the same moment go to Radiant, the host, so both phones agree. The score is last hits plus denies.
 */
internal data class MidMatch(
    val claims: Map<Int, Claim> = emptyMap(),
    val isOver: Boolean = false,
    val peerLeft: Boolean = false,
) {

    fun claim(creep: Creep, side: Side, landAt: Long): MidMatch {
        if (isOver) return this
        val deny = creep.side == side
        val new = Claim(side, landAt, deny, gold = if (deny) 0 else creep.type.gold)
        val current = claims[creep.id]
        val winner = when {
            current == null -> new
            new.landAt < current.landAt -> new
            new.landAt > current.landAt -> current
            current.side == Side.RADIANT -> current
            else -> new
        }
        return copy(claims = claims + (creep.id to winner))
    }

    fun isTaken(creepId: Int): Boolean = creepId in claims

    /** Someone's hit took the creep before [time] */
    fun takenBefore(creepId: Int, time: Long): Boolean = claims[creepId]?.let { it.landAt < time } == true

    fun lastHits(side: Side): Int = claims.values.count { it.side == side && !it.deny }

    fun denies(side: Side): Int = claims.values.count { it.side == side && it.deny }

    fun gold(side: Side): Int = claims.values.filter { it.side == side }.sumOf { it.gold }

    fun score(side: Side): Int = lastHits(side) + denies(side)

    fun end(): MidMatch = copy(isOver = true)

    fun peerLeft(): MidMatch = copy(isOver = true, peerLeft = true)
}
