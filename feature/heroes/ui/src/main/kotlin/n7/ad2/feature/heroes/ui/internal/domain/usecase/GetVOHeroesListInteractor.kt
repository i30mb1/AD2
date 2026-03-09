package n7.ad2.heroes.ui.internal.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.heroes.domain.model.Hero
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase
import n7.ad2.heroes.ui.internal.domain.vo.VOHero
import n7.ad2.ui.adapter.HeaderViewHolder
import javax.inject.Inject

internal class GetVOHeroesListUseCase @Inject constructor(private val getHeroesUseCase: GetHeroesUseCase, private val dispatchers: DispatchersProvider, private val logger: Logger) {

    operator fun invoke(): Flow<List<VOHero>> = getHeroesUseCase()
        .onStart { logger.log("heroes loaded") }
        .mapLatest { list ->
            buildList {
                list.groupBy { hero: Hero -> hero.mainAttr }
                    .forEach { (mainAttr, heroList): Map.Entry<String, List<Hero>> ->
                        add(VOHero.Header(HeaderViewHolder.Data(mainAttr)))
                        addAll(
                            heroList.map { hero ->
                                VOHero.Body(hero.name, hero.avatarUrl, hero.viewedByUser)
                            },
                        )
                    }
            }
        }
        .flowOn(dispatchers.IO)
}
