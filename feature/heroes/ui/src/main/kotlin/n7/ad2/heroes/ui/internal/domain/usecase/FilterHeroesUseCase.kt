package n7.ad2.heroes.ui.internal.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.heroes.ui.internal.domain.vo.VOHero

internal class FilterHeroesUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(list: List<VOHero>, filter: String): List<VOHero> = withContext(dispatchers.IO) {
//        list.filter { hero -> hero.name.contains(filter, true) }
        list.filter { true }
    }

}
