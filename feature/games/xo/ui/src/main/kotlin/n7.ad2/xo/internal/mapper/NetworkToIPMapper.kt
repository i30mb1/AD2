package n7.ad2.xo.internal.mapper

import n7.ad2.feature.games.xo.domain.model.Network

internal object NetworkToIPMapper: (Network) -> String {

    override fun invoke(network: Network): String {
        return when (network) {
            is Network.Available -> network.ip
            Network.Unavailable -> ""
        }
    }
}
