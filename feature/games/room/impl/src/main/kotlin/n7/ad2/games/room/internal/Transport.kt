package n7.ad2.games.room.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

internal sealed interface TransportStatus {
    data object Connecting : TransportStatus
    data class Listening(val port: Int) : TransportStatus
    data object Connected : TransportStatus
    data class Closed(val failed: Boolean) : TransportStatus
}

/** Текстовый канал до второго игрока, комната кладет в него закодированные [n7.ad2.games.room.RoomMessage] */
internal interface Transport {
    val status: StateFlow<TransportStatus>
    val incoming: Flow<String>
    fun send(text: String)
    fun close()
}
