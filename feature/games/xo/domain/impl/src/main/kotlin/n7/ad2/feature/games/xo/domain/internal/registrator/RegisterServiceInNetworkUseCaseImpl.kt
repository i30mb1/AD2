package n7.ad2.feature.games.xo.domain.internal.registrator

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import n7.ad2.app.logger.Logger
import n7.ad2.feature.games.xo.domain.RegisterServiceInNetworkUseCase
import n7.ad2.feature.games.xo.domain.model.SimpleServer

internal class RegisterServiceInNetworkUseCaseImpl(
    private val manager: NsdManager,
    private val commonSettings: CommonSettings,
    private val logger: Logger,
) : RegisterServiceInNetworkUseCase {

    private var listenersMap: MutableSet<NsdManager.RegistrationListener> = mutableSetOf()

    override suspend fun register(
        server: SimpleServer,
    ): SimpleServer = suspendCancellableCoroutine { continuation ->
        val listener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(info: NsdServiceInfo) {
                listenersMap.add(this)
                val finalName = info.serviceName // Android may have change name in order to resolve a conflict
                logger.log("DNS: registration success: $finalName")
                continuation.resume(
                    SimpleServer(name = finalName, ip = server.ip, port = server.port)
                )
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                val message = "DNS: registration failed: $errorCode"
                logger.log(message)
                continuation.resumeWithException(Exception(message))
            }

            override fun onServiceUnregistered(info: NsdServiceInfo) {
                logger.log("DNS: unregistraion success: ${info.serviceName}")
            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                val message = "DNS: unregistraion failed: $errorCode"
                logger.log(message)
            }
        }
        val nsdServiceInfo = NsdServiceInfo()
        nsdServiceInfo.serviceName = server.name
        nsdServiceInfo.serviceType = commonSettings.serviceType
        nsdServiceInfo.port = server.port
        manager.registerService(nsdServiceInfo, NsdManager.PROTOCOL_DNS_SD, listener)
        continuation.invokeOnCancellation {
            manager.unregisterService(listener)
        }
    }

    override fun unregister() {
        listenersMap.forEach(manager::unregisterService)
        listenersMap.clear()
    }
}