package n7.ad2.games.internal

import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Room

/**
 * Комната, созданная в лобби, нужна и экрану игры; фрагменты пересоздаются, поэтому держим ее на уровне процесса.
 * [onClose] закрывает то, что живет вместе с комнатой (группа Wi-Fi Direct)
 */
internal object ActiveRoom {

    @Volatile var room: Room? = null
        private set

    @Volatile var mode: GameMode = GameMode.SOLO
        private set

    private var onClose: () -> Unit = {}

    // старый onClose не зовем: новая комната может уже стоять на той же группе Wi-Fi Direct
    fun set(room: Room, mode: GameMode, onClose: () -> Unit = {}) {
        this.room?.takeIf { it !== room }?.leave()
        this.room = room
        this.mode = mode
        this.onClose = onClose
    }

    fun clear() {
        room?.leave()
        onClose()
        onClose = {}
        room = null
        mode = GameMode.SOLO
    }
}
