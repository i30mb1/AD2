package n7.ad2.games.internal

import n7.ad2.games.internal.party.Pick
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Room

/**
 * Комната сессии нужна и экрану игры; фрагменты пересоздаются, поэтому держим ее на уровне процесса.
 * Владелец комнаты - Party: экран игры ее не закрывает, а только заканчивает игру.
 * [startedAt] - момент старта матча (elapsedRealtime), общий у обоих игроков.
 * [rematch] - «Ещё раз» с экрана итогов: список игр выберет эту игру снова.
 */
internal object ActiveRoom {

    @Volatile var room: Room? = null
        private set

    @Volatile var mode: GameMode = GameMode.SOLO
        private set

    @Volatile var startedAt: Long = 0
        private set

    @Volatile private var rematch: Pick? = null

    fun set(room: Room, mode: GameMode, startedAt: Long) {
        this.room = room
        this.mode = mode
        this.startedAt = startedAt
    }

    fun clear() {
        room = null
        mode = GameMode.SOLO
        rematch = null
    }

    fun requestRematch(pick: Pick) {
        rematch = pick
    }

    fun takeRematch(): Pick? = rematch.also { rematch = null }
}
