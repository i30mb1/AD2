package n7.ad2.feature.games.xo.domain.model

sealed interface NetworkState {
    data class Connected(val ip: String) : NetworkState
    data object Disconnected : NetworkState
}
