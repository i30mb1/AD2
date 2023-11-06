package n7.ad2.xo.internal.compose

internal sealed interface XoScreenEvents {
    object StartServer: XoScreenEvents
    object ConnectToServer: XoScreenEvents
    object SendPing: XoScreenEvents
    object SendPong: XoScreenEvents
}
