package n7.ad2.feature.games.xo.domain.internal.server

internal sealed interface FrameType {
    class Text(val text: String) : FrameType
    data object Close : FrameType
}
