package n7.ad2.feature.games.xo.domain.di

import android.net.nsd.NsdManager
import n7.ad2.feature.games.xo.domain.ClientHolder
import n7.ad2.feature.games.xo.domain.DiscoverServicesInNetworkUseCase
import n7.ad2.feature.games.xo.domain.GetNetworkStateUseCase
import n7.ad2.feature.games.xo.domain.RegisterServiceInNetworkUseCase
import n7.ad2.feature.games.xo.domain.ServerHolder
import n7.ad2.feature.games.xo.domain.SocketHolder
import n7.ad2.feature.games.xo.domain.internal.registrator.CommonSettings
import n7.ad2.feature.games.xo.domain.internal.registrator.DiscoverServicesInNetworkUseCaseImpl
import n7.ad2.feature.games.xo.domain.internal.registrator.GetInfoAboutServerUseCase
import n7.ad2.feature.games.xo.domain.internal.registrator.GetNetworkStateUseCaseImpl
import n7.ad2.feature.games.xo.domain.internal.registrator.RegisterServiceInNetworkUseCaseImpl
import n7.ad2.feature.games.xo.domain.internal.server.socket.ClientHolderWithSocket
import n7.ad2.feature.games.xo.domain.internal.server.socket.ServerHolderWithSocket
import n7.ad2.feature.games.xo.domain.internal.server.socket.SocketHolderImpl

interface XoDomainComponent {
    val serverHolder: ServerHolder
    val clientHolder: ClientHolder
    val socketHolder: SocketHolder
    val registerServerInDNSUseCase: RegisterServiceInNetworkUseCase
    val discoverServicesInNetworkUseCase: DiscoverServicesInNetworkUseCase
    val getNetworkStateUseCase: GetNetworkStateUseCase
}

fun XoDomainComponent(
    dependencies: XoDomainDependencies,
): XoDomainComponent = object : XoDomainComponent {
    private val manager: NsdManager = dependencies.application.getSystemService(NsdManager::class.java)!!
    private val commonSettings = CommonSettings()
    override val registerServerInDNSUseCase = RegisterServiceInNetworkUseCaseImpl(manager, commonSettings)
    override val discoverServicesInNetworkUseCase = DiscoverServicesInNetworkUseCaseImpl(manager, commonSettings, GetInfoAboutServerUseCase(dependencies.dispatcher))
    override val serverHolder: ServerHolder = ServerHolderWithSocket(registerServerInDNSUseCase)
    override val clientHolder: ClientHolder = ClientHolderWithSocket()
    override val socketHolder: SocketHolder = SocketHolderImpl()
    override val getNetworkStateUseCase: GetNetworkStateUseCase = GetNetworkStateUseCaseImpl(dependencies.application)
}
