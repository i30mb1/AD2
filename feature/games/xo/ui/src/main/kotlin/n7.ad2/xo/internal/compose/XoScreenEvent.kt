package n7.ad2.xo.internal.compose

import n7.ad2.xo.internal.compose.model.ServerUI

internal sealed interface XoScreenEvent {
    class StartServer(val name: String): XoScreenEvent
    class ConnectToServer(val server: ServerUI): XoScreenEvent
    class SendMessage(val message: String) : XoScreenEvent
}
