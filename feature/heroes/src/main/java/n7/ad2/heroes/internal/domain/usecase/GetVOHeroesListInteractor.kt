package n7.ad2.heroes.internal.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.database_guides.internal.model.LocalHero
import n7.ad2.heroes.internal.domain.vo.VOHero
import n7.ad2.logger.AD2Logger
import n7.ad2.repositories.HeroRepository
import n7.ad2.ui.adapter.HeaderViewHolder
import javax.inject.Inject

class GetVOHeroesListUseCase @Inject constructor(
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
                            result.add(VOHero.Header(HeaderViewHolder.Data(map.key)))
                            result.addAll(map.value.map { localHero ->
                                VOHero.Body(localHero.name, HeroRepository.getFullUrlHeroImage(localHero.name), localHero.viewedByUser)
                            })
                        }
                    emit(result.toList())
                }
            }.flowOn(dispatchers.IO)
    }

}