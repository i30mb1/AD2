package n7.ad2.xo.internal.mapper

import n7.ad2.feature.games.xo.domain.model.NetworkState

internal object NetworkToIPMapper : (NetworkState) -> String {

    override fun invoke(networkState: NetworkState): String = when (networkState) {
        is NetworkState.Connected -> networkState.ip
        NetworkState.Disconnected -> ""
    }
}
