package n7.ad2.feature.games.xo.wiring

import android.app.Application
import n7.ad2.AppInformation
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.games.xo.domain.Client
import n7.ad2.feature.games.xo.domain.Server
import n7.ad2.feature.games.xo.domain.di.XoDomainComponent
import n7.ad2.feature.games.xo.domain.di.XoDomainDependencies

@dagger.Module
object XoModule {

    @dagger.Provides
    fun provideXiDomainComponent(
        res: Resources,
        dispatchers: DispatchersProvider,
        appInformation: AppInformation,
        application: Application,
        logger: Logger,
    ): XoDomainComponent = XoDomainComponent(
        object : XoDomainDependencies {
            override val application: Application = application
            override val logger = logger
            override val res: Resources = res
            override val dispatcher = dispatchers
            override val appInformation = appInformation
        }
    )

    @dagger.Provides
    fun provideServer(
        component: XoDomainComponent,
    ): Server = component.server

    @dagger.Provides
    fun provideClient(
        component: XoDomainComponent,
    ): Client = component.client
}
