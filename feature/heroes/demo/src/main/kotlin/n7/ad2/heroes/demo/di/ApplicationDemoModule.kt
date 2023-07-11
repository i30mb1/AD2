package n7.ad2.heroes.demo.di

import com.squareup.moshi.Moshi
import n7.ad2.AppInformation
import n7.ad2.AppLocale
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.common.application.BaseApplicationModule
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.database_guides.api.dao.HeroesDao
import n7.ad2.heroes.domain.di.HeroesDomainComponent
import n7.ad2.heroes.domain.di.HeroesDomainDependencies
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase
import n7.ad2.heroes.domain.usecase.UpdateStateViewedForHeroUseCase
import n7.ad2.heroes.ui.api.HeroesDependencies
import n7.ad2.navigator.Navigator

@dagger.Module(
    includes = [
        BaseApplicationModule::class,
    ]
)
internal interface ApplicationDemoModule {

    @dagger.Binds
    fun provideStreamsDependencies(impl: ApplicationComponentDemo): HeroesDependencies

    companion object {

        @dagger.Provides
        fun navigator(): Navigator = Navigator.empty()

        @dagger.Provides
        fun provideLogger(): Logger = Logger()

        @dagger.Provides
        fun provideHeroesDomainComponent(
            res: Resources,
            heroesDao: HeroesDao,
            moshi: Moshi,
            dispatchers: DispatchersProvider,
            appInformation: AppInformation,
        ): HeroesDomainComponent = HeroesDomainComponent(
            object : HeroesDomainDependencies {
                override val res: Resources = res
                override val heroesDao: HeroesDao = heroesDao
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
        fun provideAppInformation(): AppInformation = object : AppInformation {
            override val isDebug: Boolean = true
            override val appLocale: AppLocale = AppLocale.English
            override val appVersion: String = "1"
            override val isNightMode: Boolean = false
        }

        @dagger.Provides
        fun provideUpdateStateViewedForHeroUseCase(
            component: HeroesDomainComponent,
        ): UpdateStateViewedForHeroUseCase = component.updateStateViewedForHeroUseCase
    }
}
