package n7.ad2.feature.games.xo.domain.internal.registrator

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import n7.ad2.app.logger.Logger
import n7.ad2.feature.games.xo.domain.DiscoverServicesInNetworkUseCase
import n7.ad2.feature.games.xo.domain.model.Server

internal class DiscoverServicesInNetworkUseCaseImpl(
    private val manager: NsdManager,
    private val commonSettings: CommonSettings,
    private val getInfoAboutServerUseCase: GetInfoAboutServerUseCase,
    private val logger: Logger,
) : DiscoverServicesInNetworkUseCase {

    private val dispatcher = newSingleThreadContext("Discovery Server")

    override fun invoke(): Flow<List<Server>> = callbackFlow {
        val set = mutableSetOf<Server>()

        val listener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) {
                logger.log("DNS: discovery started")
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                launch(dispatcher) {
                    val server = getInfoAboutServerUseCase.resolve(manager, service)
                    logger.log("DNS: service found: ${service.serviceName}")
                    set.add(server)
                    send(set.toList())
                }
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                launch(dispatcher) {
                    logger.log("DNS: service lost: ${service.serviceName}")

                    set.removeIf { server -> server.name == service.serviceName }
                    trySend(set.toList())
                }
            }

            override fun onDiscoveryStopped(serviceType: String) {
                logger.log("DNS: discovery stopped")
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                logger.log("DNS: discovery start failed")
                manager.stopServiceDiscovery(this)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                logger.log("DNS: discovery stop failed")
                manager.stopServiceDiscovery(this)
            }
        }

        manager.discoverServices(commonSettings.serviceType, NsdManager.PROTOCOL_DNS_SD, listener)
        awaitClose {
            manager.stopServiceDiscovery(listener)
        }
    }
        .onStart { emit(emptyList()) }
        .flowOn(dispatcher)
}
