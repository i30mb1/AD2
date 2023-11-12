package n7.ad2.feature.games.xo.domain.model

sealed class Network {
    data object Unavailable : Network()
    data class Available(val ip: String) : Network()
}
