package n7.ad2.ui.heroInfo.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.Locale
import n7.ad2.data.source.local.Repository
import javax.inject.Inject

class GetJsonHeroDescriptionUseCase @Inject constructor(
        private val repository: Repository,
        private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(heroAssetsPath: String, locale: Locale) = withContext(ioDispatcher) {
       repository.getHeroDescription(heroAssetsPath, locale)
    }

}