package n7.ad2.heroes.internal.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.database_guides.internal.model.LocalHero
import n7.ad2.heroes.internal.domain.vo.VOHero
import n7.ad2.logger.Logger
import n7.ad2.repositories.HeroRepository
import n7.ad2.ui.adapter.HeaderViewHolder
import javax.inject.Inject

internal class GetVOHeroesListUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val heroRepository: HeroRepository,
    private val logger: Logger,
) {

    operator fun invoke(): Flow<List<VOHero>> {
        return heroRepository.getAllHeroes()
            .onStart { logger.log("heroes loaded") }
            .mapLatest { list ->
                val result = mutableListOf<VOHero>()
                list.groupBy { localHero: LocalHero -> localHero.mainAttr }
                    .forEach { (mainAttr, hero): Map.Entry<String, List<LocalHero>> ->
                        result.add(VOHero.Header(HeaderViewHolder.Data(mainAttr)))
                        result.addAll(hero.map { localHero ->
                            VOHero.Body(localHero.name, HeroRepository.getFullUrlHeroImage(localHero.name), localHero.viewedByUser)
                        })
                    }
                result.toList()
            }.flowOn(dispatchers.IO)
    }

}