package n7.ad.games.domain.wiring

import android.app.Application
import com.squareup.moshi.Moshi
import n7.ad2.AppInformation
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.games.domain.Client
import n7.ad2.games.domain.Server
import n7.ad2.games.domain.di.GamesDomainComponent
import n7.ad2.games.domain.di.GamesDomainDependencies

@dagger.Module
object GamesModule {

    @dagger.Provides
    fun provideGamesDomainComponent(
        res: Resources,
        moshi: Moshi,
        dispatchers: DispatchersProvider,
        appInformation: AppInformation,
        application: Application,
        logger: Logger,
    ): GamesDomainComponent = GamesDomainComponent(
        object : GamesDomainDependencies {
            //            override val application: Application = application
            override val logger = logger
            override val res: Resources = res
            override val dispatcher = dispatchers
            override val appInformation = appInformation
        }
    )

    @dagger.Provides
    fun provideServer(
        component: GamesDomainComponent,
    ): Server = component.server

    @dagger.Provides
    fun provideClient(
        component: GamesDomainComponent,
    ): Client = component.client
}
