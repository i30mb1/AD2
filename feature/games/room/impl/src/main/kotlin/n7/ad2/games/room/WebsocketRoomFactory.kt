package n7.ad2.games.room

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import n7.ad2.feature.games.xo.domain.internal.server.controller.WebsocketClientController
import n7.ad2.feature.games.xo.domain.internal.server.controller.WebsocketServerController
import n7.ad2.games.room.internal.RoomEngine
import n7.ad2.games.room.internal.WebsocketGuestTransport
import n7.ad2.games.room.internal.WebsocketHostTransport
import java.net.InetAddress

/**
 * Комнаты поверх websocket в локальной сети (Wi-Fi или группа Wi-Fi Direct).
 * Хост поднимает сервер на [host] bindAddress со случайным портом, порт виден в [RoomState.Waiting].
 */
class WebsocketRoomFactory(private val scope: CoroutineScope) : RoomFactory {

    override fun host(me: Player, bindAddress: InetAddress): Room {
        val transport = WebsocketHostTransport(WebsocketServerController(controllerScope()), scope)
        val room = RoomEngine(me, isHost = true, transport, scope)
        transport.start(me.name, bindAddress)
        return room
    }

    override fun join(me: Player, host: InetAddress, port: Int): Room {
        val transport = WebsocketGuestTransport(WebsocketClientController(controllerScope()), scope)
        val room = RoomEngine(me, isHost = false, transport, scope)
        transport.connect(me.name, host, port)
        return room
    }

    // свой Job на контроллер: stop()/disconnect() отменяют детей своего scope и не должны задеть комнату
    private fun controllerScope() = CoroutineScope(SupervisorJob(scope.coroutineContext[kotlinx.coroutines.Job]) + Dispatchers.IO)
}
