package n7.ad2.feature.heroes.ui.internal.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.heroes.ui.internal.domain.vo.VOHero
import javax.inject.Inject

internal class FilterHeroesUseCase @Inject constructor(private val dispatchers: DispatchersProvider) {

    /**
     * Оставляет героев, чьё имя содержит [filter], и заголовки только тех атрибутов, в которых кто-то нашёлся
     */
    suspend operator fun invoke(list: List<VOHero>, filter: String): List<VOHero> = withContext(dispatchers.IO) {
        if (filter.isBlank()) return@withContext list
        val query = filter.trim()
        buildList {
            var header: VOHero.Header? = null
            list.forEach { hero ->
                when (hero) {
                    is VOHero.Header -> header = hero
                    is VOHero.Body -> if (hero.name.contains(query, ignoreCase = true)) {
                        header?.let(::add)
                        header = null
                        add(hero)
                    }
                }
            }
        }
    }
}
