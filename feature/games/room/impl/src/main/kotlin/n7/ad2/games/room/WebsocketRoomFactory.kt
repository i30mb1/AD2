package n7.ad2.games.room

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import n7.ad2.feature.games.xo.domain.internal.server.controller.WebsocketClient
import n7.ad2.feature.games.xo.domain.internal.server.controller.WebsocketHub
import n7.ad2.games.room.internal.GuestRoom
import n7.ad2.games.room.internal.HostRoom
import n7.ad2.games.room.internal.WebsocketGuestTransport
import n7.ad2.games.room.internal.WebsocketHostTransport
import java.net.InetAddress

/**
 * Комнаты поверх websocket в локальной сети (Wi-Fi или группа Wi-Fi Direct).
 * Хост поднимает сервер на bindAddress со случайным портом, порт виден в [RoomState.Waiting]; гости подключаются к нему.
 */
class WebsocketRoomFactory(private val scope: CoroutineScope) : RoomFactory {

    override fun host(me: Player, bindAddress: InetAddress): Room {
        val transport = WebsocketHostTransport(WebsocketHub(ioScope()), scope)
        val room = HostRoom(me, transport, scope)
        transport.start(bindAddress)
        return room
    }

    override fun join(me: Player, host: InetAddress, port: Int): Room {
        val transport = WebsocketGuestTransport(WebsocketClient(ioScope()), scope)
        val room = GuestRoom(me, transport, scope)
        transport.connect(host, port)
        return room
    }

    // свой Job на сокеты: остановка сервера или клиента отменяет детей своего scope и не должна задеть комнату
    private fun ioScope() = CoroutineScope(SupervisorJob(scope.coroutineContext[Job]) + Dispatchers.IO)
}
