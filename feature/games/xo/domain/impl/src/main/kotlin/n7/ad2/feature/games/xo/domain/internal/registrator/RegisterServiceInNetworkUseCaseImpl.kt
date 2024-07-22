package n7.ad2.feature.games.xo.domain.internal.registrator

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import n7.ad2.feature.games.xo.domain.RegisterServiceInNetworkUseCase
import n7.ad2.feature.games.xo.domain.model.SimpleServer

internal class RegisterServiceInNetworkUseCaseImpl(
    private val manager: NsdManager,
    private val commonSettings: CommonSettings,
) : RegisterServiceInNetworkUseCase {

    override suspend operator fun invoke(server: SimpleServer): SimpleServer = suspendCancellableCoroutine { continuation ->
        val listener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(info: NsdServiceInfo) {
                val finalName = info.serviceName // Android may have change name in order to resolve a conflict
                continuation.resume(server.copy(name = finalName))
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                continuation.resumeWithException(Exception("onRegistrationFailed $serviceInfo"))
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
