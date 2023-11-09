package n7.ad2.feature.games.xo.domain

import kotlinx.coroutines.flow.Flow
import n7.ad2.feature.games.xo.domain.model.Server

interface DiscoverServicesInNetworkUseCase {
   operator fun invoke(): Flow<List<Server>>
}
