package n7.ad2.feature.games.xo.domain.di

import android.net.nsd.NsdManager
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import n7.ad2.feature.games.xo.domain.ClientHolder
import n7.ad2.feature.games.xo.domain.ConnectToWifiDirectUseCase
import n7.ad2.feature.games.xo.domain.DiscoverServicesInNetworkUseCase
import n7.ad2.feature.games.xo.domain.DiscoverServicesInWifiDirectUseCase
import n7.ad2.feature.games.xo.domain.GetDeviceNameUseCase
import n7.ad2.feature.games.xo.domain.GetNetworkStateUseCase
import n7.ad2.feature.games.xo.domain.RegisterServiceInNetworkUseCase
import n7.ad2.feature.games.xo.domain.ServerHolder
import n7.ad2.feature.games.xo.domain.internal.registrator.CommonSettings
import n7.ad2.feature.games.xo.domain.internal.registrator.ConnectToWifiDirectUseCaseImpl
import n7.ad2.feature.games.xo.domain.internal.registrator.DiscoverServicesInNetworkUseCaseImpl
import n7.ad2.feature.games.xo.domain.internal.registrator.DiscoverServicesInWifiDirectUseCaseImpl
import n7.ad2.feature.games.xo.domain.internal.registrator.GetInfoAboutServerUseCase
import n7.ad2.feature.games.xo.domain.internal.registrator.GetNetworkStateUseCaseImpl
import n7.ad2.feature.games.xo.domain.internal.registrator.RegisterServiceInNetworkUseCaseImpl
import n7.ad2.feature.games.xo.domain.internal.server.socket.ClientHolderWithSocket
import n7.ad2.feature.games.xo.domain.internal.server.socket.GameServer
import n7.ad2.feature.games.xo.domain.internal.usecase.GetDeviceNameUseCaseImpl

interface XoDomainComponent {
    val serverHolder: ServerHolder
    val clientHolder: ClientHolder
    val registerServerInDNSUseCase: RegisterServiceInNetworkUseCase
    val discoverServicesInNetworkUseCase: DiscoverServicesInNetworkUseCase
    val connectToWifiDirectUseCase: ConnectToWifiDirectUseCase
    val getNetworkStateUseCase: GetNetworkStateUseCase
    val getDeviceNameUseCase: GetDeviceNameUseCase
    val discoverServicesInWifiDirectUseCase: DiscoverServicesInWifiDirectUseCase
}

fun XoDomainComponent(
    dependencies: XoDomainDependencies,
): XoDomainComponent = object : XoDomainComponent {
    private val manager: NsdManager = dependencies.application.getSystemService(NsdManager::class.java)!!
    private val wifiP2pManager: WifiP2pManager = dependencies.application.getSystemService(WifiP2pManager::class.java)!!
    private val managerChannel: WifiP2pManager.Channel = wifiP2pManager.initialize(dependencies.application, Looper.getMainLooper(), null)
    private val commonSettings = CommonSettings()
    override val registerServerInDNSUseCase = RegisterServiceInNetworkUseCaseImpl(manager, commonSettings, dependencies.logger)
    override val discoverServicesInNetworkUseCase = DiscoverServicesInNetworkUseCaseImpl(
        manager,
        commonSettings,
        GetInfoAboutServerUseCase(dependencies.dispatcher, dependencies.logger),
        dependencies.logger,
    )
    override val serverHolder: ServerHolder = GameServer(registerServerInDNSUseCase)
    override val clientHolder: ClientHolder = ClientHolderWithSocket()
    override val discoverServicesInWifiDirectUseCase = DiscoverServicesInWifiDirectUseCaseImpl(
        dependencies.application,
        wifiP2pManager,
        managerChannel,
        dependencies.logger,
    )
    override val connectToWifiDirectUseCase = ConnectToWifiDirectUseCaseImpl(wifiP2pManager, managerChannel, dependencies.logger)
    override val getNetworkStateUseCase: GetNetworkStateUseCase = GetNetworkStateUseCaseImpl(dependencies.application)
    override val getDeviceNameUseCase: GetDeviceNameUseCase = GetDeviceNameUseCaseImpl()
}
