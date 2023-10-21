package n7.ad2.heroes.domain.wiring

import android.app.Application
import com.squareup.moshi.Moshi
import n7.ad2.AppInformation
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.heroes.domain.di.HeroesDomainComponent
import n7.ad2.heroes.domain.di.HeroesDomainDependencies
import n7.ad2.heroes.domain.usecase.GetGuideForHeroUseCase
import n7.ad2.heroes.domain.usecase.GetHeroByNameUseCase
import n7.ad2.heroes.domain.usecase.GetHeroDescriptionUseCase
import n7.ad2.heroes.domain.usecase.GetHeroSpellInputStreamUseCase
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase
import n7.ad2.heroes.domain.usecase.UpdateStateViewedForHeroUseCase

@dagger.Module
object HeroesModule {

    @dagger.Provides
    fun provideHeroesDomainComponent(
        res: Resources,
        moshi: Moshi,
        dispatchers: DispatchersProvider,
        appInformation: AppInformation,
        application: Application,
        logger: Logger,
    ): HeroesDomainComponent = HeroesDomainComponent(
        object : HeroesDomainDependencies {
            override val application: Application = application
            override val logger = logger
            override val res: Resources = res
            override val moshi: Moshi = moshi
            override val dispatcher = dispatchers
            override val appInformation = appInformation
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

    @dagger.Provides
    fun provideGetHeroSpellInputStreamUseCase(
        component: HeroesDomainComponent,
    ): GetHeroSpellInputStreamUseCase = component.getHeroSpellInputStreamUseCase

    @dagger.Provides
    fun provideUpdateStateViewedForHeroUseCase(
        component: HeroesDomainComponent,
    ): UpdateStateViewedForHeroUseCase = component.updateStateViewedForHeroUseCase

    @dagger.Provides
    fun provideGetGuideForHeroUseCase(
        component: HeroesDomainComponent,
    ): GetGuideForHeroUseCase = component.getGuideForHeroUseCase

    @dagger.Provides
    fun provideGetHeroDescriptionUseCase(
        component: HeroesDomainComponent,
    ): GetHeroDescriptionUseCase = component.getHeroDescriptionUseCase
}
