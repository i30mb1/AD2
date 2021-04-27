package n7.ad2.ui.heroes.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.heroes.domain.vo.VOHero
import javax.inject.Inject

class FilterHeroesUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(list: List<VOHero>, filter: String): List<VOHero> = withContext(ioDispatcher) {
        list.filter { it.name.contains(filter, true) }
    }

}