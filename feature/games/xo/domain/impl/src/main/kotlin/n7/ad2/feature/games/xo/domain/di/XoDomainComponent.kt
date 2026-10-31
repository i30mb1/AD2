package n7.ad2.feature.games.xo.domain.di

import android.net.nsd.NsdManager
import n7.ad2.feature.games.xo.domain.ClientCreator
import n7.ad2.feature.games.xo.domain.DiscoverServicesInNetworkUseCase
import n7.ad2.feature.games.xo.domain.GetDeviceNameUseCase
import n7.ad2.feature.games.xo.domain.GetNetworkStateUseCase
import n7.ad2.feature.games.xo.domain.RegisterServiceInNetworkUseCase
import n7.ad2.feature.games.xo.domain.ServerCreator
import n7.ad2.feature.games.xo.domain.WifiDirect
import n7.ad2.feature.games.xo.domain.internal.registrator.CommonSettings
import n7.ad2.feature.games.xo.domain.internal.registrator.DiscoverServicesInNetworkUseCaseImpl
import n7.ad2.feature.games.xo.domain.internal.registrator.GetInfoAboutServerUseCase
import n7.ad2.feature.games.xo.domain.internal.registrator.GetNetworkStateUseCaseImpl
import n7.ad2.feature.games.xo.domain.internal.registrator.RegisterServiceInNetworkUseCaseImpl
import n7.ad2.feature.games.xo.domain.internal.server.socket.ClientCreatorImpl
import n7.ad2.feature.games.xo.domain.internal.server.socket.ServerCreatorImpl
import n7.ad2.feature.games.xo.domain.internal.usecase.GetDeviceNameUseCaseImpl
import n7.ad2.feature.games.xo.domain.internal.wifidirect.WifiDirectImpl

interface XoDomainComponent {
    val serverCreator: ServerCreator
    val clientCreator: ClientCreator
    val registerServerInDNSUseCase: RegisterServiceInNetworkUseCase
    val discoverServicesInNetworkUseCase: DiscoverServicesInNetworkUseCase
    val getNetworkStateUseCase: GetNetworkStateUseCase
    val getDeviceNameUseCase: GetDeviceNameUseCase

    /** Держит канал Wi-Fi Direct: создавать один на процесс */
    val wifiDirect: WifiDirect
}

fun XoDomainComponent(dependencies: XoDomainDependencies): XoDomainComponent = object : XoDomainComponent {
    private val manager: NsdManager = dependencies.application.getSystemService(NsdManager::class.java)!!
    private val commonSettings = CommonSettings()
    override val registerServerInDNSUseCase = RegisterServiceInNetworkUseCaseImpl(manager, commonSettings, dependencies.logger)
    override val discoverServicesInNetworkUseCase = DiscoverServicesInNetworkUseCaseImpl(
        manager,
        commonSettings,
        GetInfoAboutServerUseCase(dependencies.dispatcher, dependencies.logger),
        dependencies.logger,
    )
    override val serverCreator: ServerCreator = ServerCreatorImpl()
    override val clientCreator: ClientCreator = ClientCreatorImpl()
    override val wifiDirect: WifiDirect by lazy { WifiDirectImpl(dependencies.application, dependencies.logger) }
    override val getNetworkStateUseCase: GetNetworkStateUseCase = GetNetworkStateUseCaseImpl(dependencies.application)
    override val getDeviceNameUseCase: GetDeviceNameUseCase = GetDeviceNameUseCaseImpl()
}
