package n7.ad2.di

import com.squareup.moshi.Moshi
import n7.ad2.Resources
import n7.ad2.database_guides.api.dao.HeroesDao
import n7.ad2.heroes.domain.GetHeroByNameUseCase
import n7.ad2.heroes.domain.GetHeroesUseCase
import n7.ad2.heroes.domain.di.HeroesDomainComponent
import n7.ad2.heroes.domain.di.HeroesDomainDependencies

@dagger.Module
object HeroesModule {

    @dagger.Provides
    fun provideHeroesDomainComponent(
        res: Resources,
        heroesDao: HeroesDao,
        moshi: Moshi,
    ): HeroesDomainComponent = HeroesDomainComponent(
        object : HeroesDomainDependencies {
            override val res: Resources = res
            override val heroesDao: HeroesDao = heroesDao
            override val moshi: Moshi = moshi
        }
    )

    @dagger.Provides
    fun provideGetHeroesUseCase(
        component: HeroesDomainComponent,
    ): GetHeroesUseCase = component.getHeroesUseCase

    @dagger.Provides
    fun provideGetHeroByNameUseCase(
        component: HeroesDomainComponent,
    ): GetHeroByNameUseCase = component.getHeroByNameUseCase
}
