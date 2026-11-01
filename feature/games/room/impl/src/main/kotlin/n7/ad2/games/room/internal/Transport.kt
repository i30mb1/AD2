package n7.ad2.games.room.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/** Что происходит с подключением гостя, в том порядке, в каком случилось */
internal sealed interface TransportEvent {
    data object Connected : TransportEvent
    data class Text(val text: String) : TransportEvent

    /** [failed] - подключиться не удалось вовсе */
    data class Closed(val failed: Boolean) : TransportEvent
}

/** Текстовый канал гостя до хоста, комната кладет в него закодированные [n7.ad2.games.room.RoomMessage] */
internal interface Transport {
    val events: Flow<TransportEvent>
    fun send(text: String)
    fun close()
}

internal sealed interface HostStatus {
    data object Starting : HostStatus
    data class Listening(val port: Int) : HostStatus
    data class Stopped(val failed: Boolean) : HostStatus
}

/** Подключения к хосту по номерам: подключился, прислал текст, отключился */
internal sealed interface HostEvent {
    data class Joined(val client: Int) : HostEvent
    data class Text(val client: Int, val text: String) : HostEvent
    data class Left(val client: Int) : HostEvent
}

/** Сервер хоста: тексты от каждого подключения и отправка любому из них; [send], [drop] и [close] выполняются по порядку вызова */
internal interface HostTransport {
    val status: StateFlow<HostStatus>
    val events: Flow<HostEvent>
    fun send(client: Int, text: String)

    /** Закрыть одно подключение после уже отправленного ему */
    fun drop(client: Int)
    fun close()
}
