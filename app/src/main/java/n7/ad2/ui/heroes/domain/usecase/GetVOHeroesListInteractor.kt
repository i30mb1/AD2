package n7.ad2.ui.heroes.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import n7.ad2.base.adapter.HeaderViewHolder
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.data.source.local.HeroRepository
import n7.ad2.database_heroes.api.model.LocalHero
import n7.ad2.logger.AD2Logger
import n7.ad2.ui.heroes.domain.vo.VOHero
import n7.ad2.ui.heroes.domain.vo.VOHeroBody
import n7.ad2.ui.heroes.domain.vo.VOHeroHeader
import javax.inject.Inject

class GetVOHeroesListInteractor @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val heroRepository: HeroRepository,
    private val logger: AD2Logger,
) {

    operator fun invoke(): Flow<List<VOHero>> {
        return heroRepository.getAllHeroes()
            .onStart { logger.log("heroes loaded") }
            .flatMapLatest { list ->
                val result = mutableListOf<VOHero>()
                flow {
                    list.groupBy { localHero: LocalHero -> localHero.mainAttr }
                        .forEach { map: Map.Entry<String, List<LocalHero>> ->
                            result.add(VOHeroHeader(HeaderViewHolder.Data(map.key)))
                            result.addAll(map.value.map { localHero ->
                                VOHeroBody(localHero.name, HeroRepository.getFullUrlHeroImage(localHero.name), localHero.viewedByUser)
                            })
                        }
                    emit(result.toList())
                }
            }.flowOn(dispatchers.IO)
    }

}