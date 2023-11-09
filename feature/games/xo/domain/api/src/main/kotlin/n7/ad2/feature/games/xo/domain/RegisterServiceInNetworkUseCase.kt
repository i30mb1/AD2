package n7.ad2.feature.games.xo.domain

import n7.ad2.feature.games.xo.domain.model.Server

interface RegisterServiceInNetworkUseCase {
    suspend operator fun invoke(server: Server): Server
}
