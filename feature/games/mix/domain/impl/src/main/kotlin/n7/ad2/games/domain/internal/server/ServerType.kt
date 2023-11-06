package n7.ad2.games.domain.internal.server

sealed interface ServerType {
    object Socket: ServerType
}
