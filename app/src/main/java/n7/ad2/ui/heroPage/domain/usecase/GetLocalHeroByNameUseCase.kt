package n7.ad2.ui.heroPage.domain.usecase

import ad2.n7.coroutines.DispatchersProvider
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.HeroRepository
import n7.ad2.data.source.local.model.LocalHero
import javax.inject.Inject

class GetLocalHeroByNameUseCase @Inject constructor(
    private val heroRepository: HeroRepository,
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(name: String): LocalHero = withContext(dispatchers.Default) {
        heroRepository.getHero(name)
    }

}