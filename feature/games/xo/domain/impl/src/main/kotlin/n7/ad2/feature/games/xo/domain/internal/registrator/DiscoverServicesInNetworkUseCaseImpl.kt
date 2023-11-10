package n7.ad2.feature.games.xo.domain.internal.registrator

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import n7.ad2.feature.games.xo.domain.DiscoverServicesInNetworkUseCase
import n7.ad2.feature.games.xo.domain.model.Server

internal class DiscoverServicesInNetworkUseCaseImpl(
    private val manager: NsdManager,
    private val commonSettings: CommonSettings,
    private val getInfoAboutServerUseCase: GetInfoAboutServerUseCase,
) : DiscoverServicesInNetworkUseCase {

    private val dispatcher = newSingleThreadContext("DiscoverServer")

    override fun invoke(): Flow<List<Server>> = callbackFlow {
        val set = mutableSetOf<Server>()

        val listener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) = Unit

            override fun onServiceFound(service: NsdServiceInfo) {
                launch(dispatcher) {
                    val server = getInfoAboutServerUseCase.resolve(manager, service)

                    set.add(server)
                    send(set.toList())
                }
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                set.removeIf { server -> server.port == service.port }
                trySend(set.toList())
            }

            override fun onDiscoveryStopped(serviceType: String) = Unit

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                manager.stopServiceDiscovery(this)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                manager.stopServiceDiscovery(this)
            }
        }

        manager.discoverServices(commonSettings.serviceType, NsdManager.PROTOCOL_DNS_SD, listener)
        awaitClose { manager.stopServiceDiscovery(listener) }
    }
        .flowOn(dispatcher)
}
