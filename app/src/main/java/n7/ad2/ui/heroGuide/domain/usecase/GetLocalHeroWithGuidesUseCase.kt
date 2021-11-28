package n7.ad2.ui.heroGuide.domain.usecase

import ad2.n7.coroutines.DispatchersProvider
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import n7.ad2.data.source.local.HeroRepository
import javax.inject.Inject

class GetLocalHeroWithGuidesUseCase @Inject constructor(
    private val heroRepository: HeroRepository,
    private val dispatchers: DispatchersProvider,
) {

    operator fun invoke(heroName: String) = heroRepository.getHeroWithGuides(heroName)
        .distinctUntilChanged()
        .flowOn(dispatchers.IO)

}