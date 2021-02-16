package n7.ad2.ui.heroGuide.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import n7.ad2.data.source.local.HeroRepository
import javax.inject.Inject

class GetLocalHeroWithGuidesUseCase @Inject constructor(
    private val heroRepository: HeroRepository,
    private val ioDispatcher: CoroutineDispatcher,
) {

    operator fun invoke(heroName: String) = heroRepository.getHeroWithGuides(heroName)
        .distinctUntilChanged()
        .flowOn(ioDispatcher)

}