package n7.ad2.heropage.internal.guides.domain.usecase

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import n7.ad2.coroutines.DispatchersProvider
import javax.inject.Inject

class GetLocalHeroWithGuidesUseCase @Inject constructor(
    private val heroRepository: n7.ad2.repositories.HeroRepository,
    private val dispatchers: DispatchersProvider,
) {

    operator fun invoke(heroName: String) = heroRepository.getHeroWithGuides(heroName)
        .distinctUntilChanged()
        .flowOn(dispatchers.IO)

}