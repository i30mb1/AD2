package n7.ad2.xo.internal

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import androidx.annotation.RequiresApi
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import n7.ad2.xo.internal.model.ServerUI

internal class DiscoverServer @Inject constructor(
    private val discoverSettings: DiscoverSettings,
) {

    fun discover(manager: NsdManager): Flow<ServerUI> = callbackFlow {
        val list = mutableListOf<ServerUI>()
        val listener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) = Unit

            override fun onServiceFound(service: NsdServiceInfo) {
                Log.d("N7", "Service discovery success$service")

//                manager.registerServiceInfoCallback(service, Executors.newSingleThreadExecutor(), serviceInfo)
                val resolveListener = object : NsdManager.ResolveListener {
                    override fun onResolveFailed(p0: NsdServiceInfo, p1: Int) {
                        println("")
                    }

                    override fun onServiceResolved(service: NsdServiceInfo) {
                        list.add(ServerUI(service.host.hostAddress, service.port, service.serviceName))
                    }
                }
                manager.resolveService(service, resolveListener)
            }

            override fun onServiceLost(service: NsdServiceInfo) = Unit

            override fun onDiscoveryStopped(serviceType: String) = Unit

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                manager.stopServiceDiscovery(this)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                manager.stopServiceDiscovery(this)
            }
        }

        manager.discoverServices(discoverSettings.serviceType, NsdManager.PROTOCOL_DNS_SD, listener)
        awaitClose { manager.stopServiceDiscovery(listener) }
    }
}

internal suspend fun getServerInfo(manager: NsdManager) = suspendCancellableCoroutine<ServerUI> {
//    manager.resolveService()
}

@RequiresApi(34)
val serviceInfo =  object : NsdManager.ServiceInfoCallback {
    override fun onServiceInfoCallbackRegistrationFailed(p0: Int) {
        Log.d("N7", "$p0")
    }

    override fun onServiceUpdated(p0: NsdServiceInfo) {
        Log.d("N7", "$p0")
    }

    override fun onServiceLost() {
        Log.d("N7", "onServiceLost")
    }

    override fun onServiceInfoCallbackUnregistered() {
        Log.d("N7", "onServiceInfoCallbackUnregistered")
    }
}
