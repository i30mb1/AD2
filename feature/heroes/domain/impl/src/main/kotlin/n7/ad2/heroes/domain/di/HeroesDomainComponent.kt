package n7.ad2.heroes.domain.di

import n7.ad2.heroes.domain.GetHeroByNameUseCase
import n7.ad2.heroes.domain.GetHeroSpellInputStreamUseCase
import n7.ad2.heroes.domain.GetHeroesUseCase
import n7.ad2.heroes.domain.UpdateStateViewedForHeroUseCase
import n7.ad2.heroes.domain.internal.GetHeroByNameUseCaseImpl
import n7.ad2.heroes.domain.internal.GetHeroSpellInputStreamUseCaseImpl
import n7.ad2.heroes.domain.internal.GetHeroesUseCaseImpl
import n7.ad2.heroes.domain.internal.UpdateStateViewedForHeroUseCaseImpl
import n7.ad2.heroes.domain.internal.data.HeroesRepositoryImpl

interface HeroesDomainComponent {

    val getHeroesUseCase: GetHeroesUseCase
    val getHeroByNameUseCase: GetHeroByNameUseCase
    val getHeroSpellInputStreamUseCase: GetHeroSpellInputStreamUseCase
    val updateStateViewedForHeroUseCase: UpdateStateViewedForHeroUseCase
}

fun HeroesDomainComponent(
    dependencies: HeroesDomainDependencies,
): HeroesDomainComponent = object : HeroesDomainComponent {

    private val heroesRepository = HeroesRepositoryImpl(
        dependencies.res,
        dependencies.heroesDao,
        dependencies.moshi,
    )

    override val getHeroesUseCase = GetHeroesUseCaseImpl(heroesRepository)

    override val getHeroByNameUseCase = GetHeroByNameUseCaseImpl(heroesRepository)

    override val getHeroSpellInputStreamUseCase = GetHeroSpellInputStreamUseCaseImpl(heroesRepository)

    override val updateStateViewedForHeroUseCase = UpdateStateViewedForHeroUseCaseImpl(heroesRepository)
}
