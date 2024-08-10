package n7.ad2.xo.internal.mapper

import n7.ad2.feature.games.xo.domain.model.Server
import n7.ad2.xo.internal.compose.model.ServerUI

internal object ServerToServerUIMapper : (Server, String) -> ServerUI {

    override fun invoke(server: Server, myDeviceIp: String): ServerUI {
        return ServerUI(server.name, server.ip, server.port.toString(), server.ip == myDeviceIp)
    }
}
