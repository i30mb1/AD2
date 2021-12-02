package n7.ad2.heroes.internal.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.heroes.internal.domain.vo.VOHero
import javax.inject.Inject

internal class FilterHeroesUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(list: List<VOHero>, filter: String): List<VOHero> = withContext(dispatchers.IO) {
//        list.filter { hero -> hero.name.contains(filter, true) }
        list.filter { true }
    }

}