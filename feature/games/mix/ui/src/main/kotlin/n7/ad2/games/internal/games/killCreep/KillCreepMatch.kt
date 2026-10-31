package n7.ad2.games.internal.games.killCreep

import n7.ad2.games.room.GameMode

/** Кто забрал крипа: [mine] - я, [timeMs] - момент тапа от начала игры (для спора за одного крипа) */
internal data class Take(val mine: Boolean, val gold: Int, val timeMs: Long)

/**
 * Правила Kill Creep:
 * - SOLO: золото за добивания; ранний тап - промах; [SOLO_MISSES] промахов или время - конец;
 *   крип, добитый союзниками, - упущен ([lost]): золота нет, но игру не заканчивает (крипов больше, чем успеть - так задумано);
 * - VERSUS: у каждого свое золото, лимита промахов нет, конец по времени; спор за одного крипа выигрывает более ранний тап,
 *   при равенстве - хост (правило одинаковое на обоих телефонах, поэтому счет сходится);
 * - COOP: общее золото и общие промахи, [COOP_MISSES] промахов или время - конец.
 */
internal data class KillCreepMatch(
    val mode: GameMode,
    val isHost: Boolean,
    val taken: Map<Int, Take>,
    val misses: Int,
    val lost: Int,
    val isOver: Boolean,
    val peerLeft: Boolean,
) {

    val myGold: Int get() = taken.values.filter { it.mine }.sumOf { it.gold }
    val peerGold: Int get() = taken.values.filterNot { it.mine }.sumOf { it.gold }
    val score: Int get() = if (mode == GameMode.COOP) myGold + peerGold else myGold
    val kills: Int get() = taken.values.count { it.mine }

    fun isTaken(id: Int): Boolean = id in taken

    fun myKill(id: Int, gold: Int, timeMs: Long): KillCreepMatch = claim(id, Take(mine = true, gold, timeMs))

    fun peerKill(id: Int, gold: Int, timeMs: Long): KillCreepMatch = claim(id, Take(mine = false, gold, timeMs))

    fun myEarly(): KillCreepMatch = miss(countsInMode = mode != GameMode.VERSUS)

    fun peerEarly(): KillCreepMatch = miss(countsInMode = mode == GameMode.COOP)

    /** Союзники добили крипа: если его никто не забрал - упущен */
    fun allyKill(id: Int): KillCreepMatch = if (isTaken(id) || isOver) this else copy(lost = lost + 1)

    fun timeUp(): KillCreepMatch = copy(isOver = true)

    fun peerLeft(): KillCreepMatch = copy(isOver = true, peerLeft = true)

    private fun claim(id: Int, take: Take): KillCreepMatch {
        if (isOver) return this
        val current = taken[id] ?: return copy(taken = taken + (id to take))
        if (current.mine == take.mine) return this
        val hostTake = if (isHost == take.mine) take else current
        val winner = when {
            take.timeMs < current.timeMs -> take
            take.timeMs > current.timeMs -> current
            else -> hostTake
        }
        return copy(taken = taken + (id to winner))
    }

    private fun miss(countsInMode: Boolean): KillCreepMatch {
        if (isOver || !countsInMode) return this
        val misses = misses + 1
        val limit = when (mode) {
            GameMode.SOLO -> SOLO_MISSES
            GameMode.COOP -> COOP_MISSES
            GameMode.VERSUS -> Int.MAX_VALUE
        }
        return copy(misses = misses, isOver = misses >= limit)
    }

    companion object {
        const val SOLO_MISSES = 5
        const val COOP_MISSES = 8

        fun start(mode: GameMode, isHost: Boolean) = KillCreepMatch(mode, isHost, emptyMap(), misses = 0, lost = 0, isOver = false, peerLeft = false)
    }
}
