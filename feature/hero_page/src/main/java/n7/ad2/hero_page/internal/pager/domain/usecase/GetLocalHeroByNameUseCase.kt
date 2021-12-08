package n7.ad2.hero_page.internal.pager.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.database_guides.internal.model.LocalHero
import n7.ad2.repositories.HeroRepository
import javax.inject.Inject

class GetLocalHeroByNameUseCase @Inject constructor(
    private val heroRepository: HeroRepository,
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(name: String): LocalHero = withContext(dispatchers.Default) {
        heroRepository.getHero(name)
    }

}