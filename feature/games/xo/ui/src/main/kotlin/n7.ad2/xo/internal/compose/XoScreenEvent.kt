package n7.ad2.xo.internal.compose

internal sealed interface XoScreenEvent {
    object StartServer: XoScreenEvent
    class ConnectToServer(val ip: String): XoScreenEvent
    object SendPing: XoScreenEvent
    object SendPong: XoScreenEvent
}
