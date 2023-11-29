package n7.ad2.heroes.domain.di

import n7.ad2.heroes.domain.internal.data.HeroesRepositoryImpl
import n7.ad2.heroes.domain.internal.data.db.HeroesDatabase
import n7.ad2.heroes.domain.internal.usecase.GetGuideForHeroUseCaseImpl
import n7.ad2.heroes.domain.internal.usecase.GetHeroByNameUseCaseImpl
import n7.ad2.heroes.domain.internal.usecase.GetHeroDescriptionUseCaseImpl
import n7.ad2.heroes.domain.internal.usecase.GetHeroSpellInputStreamUseCaseImpl
import n7.ad2.heroes.domain.internal.usecase.GetHeroesUseCaseImpl
import n7.ad2.heroes.domain.internal.usecase.UpdateStateViewedForHeroUseCaseImpl
import n7.ad2.heroes.domain.usecase.GetGuideForHeroUseCase
import n7.ad2.heroes.domain.usecase.GetHeroByNameUseCase
import n7.ad2.heroes.domain.usecase.GetHeroDescriptionUseCase
import n7.ad2.heroes.domain.usecase.GetHeroSpellInputStreamUseCase
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase
import n7.ad2.heroes.domain.usecase.UpdateStateViewedForHeroUseCase

interface HeroesDomainComponent {

    val getHeroesUseCase: GetHeroesUseCase
    val getHeroByNameUseCase: GetHeroByNameUseCase
    val getHeroSpellInputStreamUseCase: GetHeroSpellInputStreamUseCase
    val updateStateViewedForHeroUseCase: UpdateStateViewedForHeroUseCase
    val getGuideForHeroUseCase: GetGuideForHeroUseCase
    val getHeroDescriptionUseCase: GetHeroDescriptionUseCase
}

fun HeroesDomainComponent(
    dependencies: HeroesDomainDependencies,
): HeroesDomainComponent = object : HeroesDomainComponent {

    private val database = HeroesDatabase.getInstance(dependencies.application)
    private val heroesDao = database.heroesDao
    private val heroesRepository = HeroesRepositoryImpl(heroesDao)

    override val getHeroesUseCase = GetHeroesUseCaseImpl(heroesRepository)

    override val getHeroByNameUseCase = GetHeroByNameUseCaseImpl(heroesRepository)

    override val getHeroSpellInputStreamUseCase = GetHeroSpellInputStreamUseCaseImpl(heroesRepository)

    override val updateStateViewedForHeroUseCase = UpdateStateViewedForHeroUseCaseImpl(heroesRepository, dependencies.dispatcher)

    override val getGuideForHeroUseCase = GetGuideForHeroUseCaseImpl(heroesRepository)

    override val getHeroDescriptionUseCase = GetHeroDescriptionUseCaseImpl(heroesRepository)
}
