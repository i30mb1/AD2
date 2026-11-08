package n7.ad2.feature.heroes.ui.internal.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.heroes.domain.model.Hero
import n7.ad2.heroes.domain.model.HeroAttribute
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase
import n7.ad2.feature.heroes.ui.internal.domain.vo.VOHero
import javax.inject.Inject

internal class GetVOHeroesListUseCase @Inject constructor(private val getHeroesUseCase: GetHeroesUseCase, private val dispatchers: DispatchersProvider, private val logger: Logger) {

    /** Attributes in the order of the game's hero grid, heroes by name in each: the database keeps them in the order rows were added. */
    operator fun invoke(): Flow<List<VOHero>> = getHeroesUseCase()
        .onStart { logger.log("heroes loaded") }
        .mapLatest { list ->
            list.groupBy { hero: Hero -> hero.mainAttr.toAttribute() }
                .toSortedMap()
                .flatMap { (attribute, heroes) ->
                    listOf(VOHero.Header(attribute)) + heroes.sortedBy { it.name }.map { hero -> VOHero.Body(hero.name, hero.avatarUrl, hero.viewedByUser) }
                }
        }
        .flowOn(dispatchers.IO)
}

/** The database keeps the attribute as the game names it, the 2023 rows capitalized. */
private fun String.toAttribute(): HeroAttribute = HeroAttribute.entries.firstOrNull { it.name.equals(this, ignoreCase = true) } ?: HeroAttribute.UNIVERSAL
