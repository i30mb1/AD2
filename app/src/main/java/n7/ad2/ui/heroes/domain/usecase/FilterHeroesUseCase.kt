package n7.ad2.ui.heroes.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.ui.heroes.domain.vo.VOHero
import javax.inject.Inject

class FilterHeroesUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(list: List<VOHero>, filter: String): List<VOHero> = withContext(dispatchers.IO) {
//        list.filter { it.name.contains(filter, true) }
        list.filter { true }
    }

}