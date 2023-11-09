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
import n7.ad2.xo.internal.model.AvailableServer

internal class DiscoverServer @Inject constructor(
    private val discoverSettings: DiscoverSettings,
    private val resolveDiscoveredServer: ResolveDiscoveredServer,
) {

    fun discover(manager: NsdManager): Flow<AvailableServer> = callbackFlow {
        val list = mutableSetOf<AvailableServer>()

        val listener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) = Unit

            override fun onServiceFound(service: NsdServiceInfo) {
                val server = resolveDiscoveredServer.resolve(service)
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
