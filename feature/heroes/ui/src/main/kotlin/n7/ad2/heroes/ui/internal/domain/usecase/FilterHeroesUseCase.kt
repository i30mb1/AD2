package n7.ad2.heroes.ui.internal.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.heroes.ui.internal.domain.vo.VOHero
import javax.inject.Inject

internal class FilterHeroesUseCase @Inject constructor(private val dispatchers: DispatchersProvider) {

    suspend operator fun invoke(list: List<VOHero>, filter: String): List<VOHero> = withContext(dispatchers.IO) {
        if (filter.isEmpty()) return@withContext list
        list.filter { hero ->
            when (hero) {
                is VOHero.Body -> hero.name.contains(filter, ignoreCase = true)
                is VOHero.Header -> false
            }
        }
    }
}
