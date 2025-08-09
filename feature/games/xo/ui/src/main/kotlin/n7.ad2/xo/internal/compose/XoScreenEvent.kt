package n7.ad2.xo.internal.compose

import n7.ad2.xo.internal.compose.model.ServerUI
import n7.ad2.xo.internal.model.SocketType

internal sealed interface XoScreenEvent {
    class StartServer(val name: String): XoScreenEvent
    class ConnectToServer(val server: ServerUI): XoScreenEvent
    class SendMessage(val message: String) : XoScreenEvent
    class SelectSocketType(val socketType: SocketType) : XoScreenEvent
}
