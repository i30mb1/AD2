package n7.ad2.feature.games.xo.domain.internal.registrator

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import n7.ad2.app.logger.Logger
import n7.ad2.feature.games.xo.domain.RegisterServiceInNetworkUseCase
import n7.ad2.feature.games.xo.domain.model.SimpleSocketServer

internal class RegisterServiceInNetworkUseCaseImpl(
    private val manager: NsdManager,
    private val commonSettings: CommonSettings,
    private val logger: Logger,
) : RegisterServiceInNetworkUseCase {

    override suspend operator fun invoke(
        server: SimpleSocketServer,
    ): SimpleSocketServer = suspendCancellableCoroutine { continuation ->
        val listener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(info: NsdServiceInfo) {
                val finalName = info.serviceName // Android may have change name in order to resolve a conflict
                logger.log("Registration in DNS success")
                continuation.resume(server.copy(name = finalName))
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                val message = "Registration in DNS failed. $serviceInfo"
                logger.log(message)
                continuation.resumeWithException(Exception(message))
            }

            override fun onServiceUnregistered(arg0: NsdServiceInfo) = Unit

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) = Unit
        }
        val nsdServiceInfo = NsdServiceInfo()
        nsdServiceInfo.serviceName = server.name
        nsdServiceInfo.serviceType = commonSettings.serviceType
        nsdServiceInfo.port = server.port
        manager.registerService(nsdServiceInfo, NsdManager.PROTOCOL_DNS_SD, listener)
        continuation.invokeOnCancellation { manager.unregisterService(listener) }
    }
}
