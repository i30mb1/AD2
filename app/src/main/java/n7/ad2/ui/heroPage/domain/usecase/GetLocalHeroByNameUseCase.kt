package n7.ad2.ui.heroPage.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.Repository
import n7.ad2.data.source.local.model.LocalHero
import javax.inject.Inject

class GetLocalHeroByNameUseCase @Inject constructor(
        private val repository: Repository,
        private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(name: String): LocalHero = withContext(ioDispatcher) {
        repository.getHero(name)
    }

}