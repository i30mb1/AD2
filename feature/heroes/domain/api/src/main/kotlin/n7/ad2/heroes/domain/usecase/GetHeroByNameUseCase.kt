package n7.ad2.heroes.domain.usecase

import n7.ad2.heroes.domain.model.Hero

interface GetHeroByNameUseCase {

    suspend operator fun invoke(name: String): Hero
}
