package n7.ad2.feature.games.xo.wiring

import android.app.Application
import n7.ad2.AppInformation
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.games.xo.domain.ClientCreator
import n7.ad2.feature.games.xo.domain.ConnectToWifiDirectUseCase
import n7.ad2.feature.games.xo.domain.DiscoverServicesInNetworkUseCase
import n7.ad2.feature.games.xo.domain.DiscoverServicesInWifiDirectUseCase
import n7.ad2.feature.games.xo.domain.GetDeviceNameUseCase
import n7.ad2.feature.games.xo.domain.GetNetworkStateUseCase
import n7.ad2.feature.games.xo.domain.RegisterServiceInNetworkUseCase
import n7.ad2.feature.games.xo.domain.ServerCreator
import n7.ad2.feature.games.xo.domain.di.XoDomainComponent
import n7.ad2.feature.games.xo.domain.di.XoDomainDependencies

@dagger.Module
object XoModule {

    @dagger.Provides
    fun provideXiDomainComponent(res: Resources, dispatchers: DispatchersProvider, appInformation: AppInformation, application: Application, logger: Logger): XoDomainComponent = XoDomainComponent(
        object : XoDomainDependencies {
            override val application: Application = application
            override val logger = logger
            override val res: Resources = res
            override val dispatcher = dispatchers
            override val appInformation = appInformation
        },
    )

    @dagger.Provides
    fun provideServer(component: XoDomainComponent): ServerCreator = component.serverCreator

    @dagger.Provides
    fun provideClient(component: XoDomainComponent): ClientCreator = component.clientCreator

    @dagger.Provides
    fun provideGetNetworkStateUseCase(component: XoDomainComponent): GetNetworkStateUseCase = component.getNetworkStateUseCase

    @dagger.Provides
    fun provideConnectToWifiDirectUseCase(component: XoDomainComponent): ConnectToWifiDirectUseCase = component.connectToWifiDirectUseCase

    @dagger.Provides
    fun provideGetDeviceNameUseCase(component: XoDomainComponent): GetDeviceNameUseCase = component.getDeviceNameUseCase

    @dagger.Provides
    fun provideRegisterServiceInNetworkUseCase(component: XoDomainComponent): RegisterServiceInNetworkUseCase = component.registerServerInDNSUseCase

    @dagger.Provides
    fun provideDiscoverServicesInNetworkUseCase(component: XoDomainComponent): DiscoverServicesInNetworkUseCase = component.discoverServicesInNetworkUseCase

    @dagger.Provides
    fun provideDiscoverServicesInWifiDirectUseCase(component: XoDomainComponent): DiscoverServicesInWifiDirectUseCase = component.discoverServicesInWifiDirectUseCase
}
