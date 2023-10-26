package n7.ad2.games.domain.internal.server

internal sealed interface FrameType {
    class Text(val text: String) : FrameType
    object Close : FrameType
}
