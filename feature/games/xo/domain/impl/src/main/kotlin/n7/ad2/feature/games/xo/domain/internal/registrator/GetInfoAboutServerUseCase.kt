@file:Suppress("DEPRECATION")

package n7.ad2.feature.games.xo.domain.internal.registrator

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.suspendCancellableCoroutine
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.games.xo.domain.model.Server

internal class GetInfoAboutServerUseCase(
    private val dispatchers: DispatchersProvider,
) {

    suspend fun resolve(
        manager: NsdManager,
        service: NsdServiceInfo,
    ) = suspendCancellableCoroutine { continuation ->
        when {
            Build.VERSION.SDK_INT >= 34 -> {
                val callback = object : NsdManager.ServiceInfoCallback {
                    override fun onServiceInfoCallbackRegistrationFailed(p0: Int) {
                        continuation.resumeWithException(Exception("onServiceInfoCallbackRegistrationFailed"))
                    }

                    override fun onServiceUpdated(info: NsdServiceInfo) {
                        val server = Server(
                            info.serviceName,
                            info.hostAddresses.firstOrNull()?.hostAddress ?: info.host.hostAddress,
                            info.port,
                        )
                        continuation.resume(server)
                    }

                    override fun onServiceLost() = Unit
                    override fun onServiceInfoCallbackUnregistered() = Unit
                }
                manager.registerServiceInfoCallback(service, dispatchers.Default.asExecutor(), callback)
                continuation.invokeOnCancellation { manager.unregisterServiceInfoCallback(callback) }
            }

            else -> {
                val callback = object : NsdManager.ResolveListener {
                    override fun onResolveFailed(p0: NsdServiceInfo, p1: Int) {
                        continuation.resumeWithException(Exception("onServiceInfoCallbackRegistrationFailed"))
                    }

                    override fun onServiceResolved(info: NsdServiceInfo) {
                        val server = Server(
                            info.serviceName,
                            info.host.hostAddress!!,
                            info.port,
                        )
                        continuation.resume(server)
                    }
                }
                manager.resolveService(service, callback)
            }
        }
    }
}
