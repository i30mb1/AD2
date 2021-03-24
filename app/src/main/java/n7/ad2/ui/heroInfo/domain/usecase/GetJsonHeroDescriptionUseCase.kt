package n7.ad2.ui.heroInfo.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.Locale
import n7.ad2.data.source.local.HeroRepository
import javax.inject.Inject

class GetJsonHeroDescriptionUseCase @Inject constructor(
    private val heroRepository: HeroRepository,
    private val ioDispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(heroName: String, locale: Locale) = withContext(ioDispatcher) {
        heroRepository.getHeroDescription(heroName, locale)
    }

}