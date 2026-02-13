package n7.ad2.feature.games.xo.domain.internal.server

sealed interface ServerType {
    object Socket : ServerType
}
