package n7.ad2.heroes.domain.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import n7.ad2.heroes.domain.GetHeroesUseCase
import n7.ad2.heroes.domain.Hero
import n7.ad2.heroes.domain.internal.data.LocalHeroToHeroMapper

internal class GetHeroesUseCaseImpl(
    private val heroesRepository: HeroesRepository,
) : GetHeroesUseCase {

    override fun invoke(): Flow<List<Hero>> {
        return heroesRepository.getAllHeroes()
            .map { localHeroList ->
                localHeroList.map(LocalHeroToHeroMapper)
            }
    }
}
