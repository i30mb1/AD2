package n7.ad2.games.internal

import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Room

/**
 * Комната, созданная в лобби, нужна и экрану игры; фрагменты пересоздаются, поэтому держим ее на уровне процесса
 */
internal object ActiveRoom {

    @Volatile var room: Room? = null
        private set

    @Volatile var mode: GameMode = GameMode.SOLO
        private set

    fun set(room: Room, mode: GameMode) {
        this.room?.takeIf { it !== room }?.leave()
        this.room = room
        this.mode = mode
    }

    fun clear() {
        room?.leave()
        room = null
        mode = GameMode.SOLO
    }
}
