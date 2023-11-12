package n7.ad2.feature.games.xo.domain.internal.registrator

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import java.net.NetworkInterface
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import n7.ad2.feature.games.xo.domain.GetNetworkStateUseCase
import n7.ad2.feature.games.xo.domain.model.Network

@SuppressLint("MissingPermission")
class GetNetworkStateUseCaseImpl(
    context: Context,
) : GetNetworkStateUseCase {

    private val connectivityManager: ConnectivityManager = context.getSystemService(ConnectivityManager::class.java)!!

    override fun invoke(): Flow<Network> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                trySend(Network.Available(getIPAddress()))
            }

            override fun onUnavailable() {
                trySend(Network.Unavailable)
            }

            override fun onLost(network: android.net.Network) {
                trySend(Network.Unavailable)
            }

            override fun onCapabilitiesChanged(network: android.net.Network, networkCapabilities: NetworkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities)
            }
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
        awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
    }

    private fun getIPAddress(): String {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            val inetAddresses = networkInterface.inetAddresses
            while (inetAddresses.hasMoreElements()) {
                val adress = inetAddresses.nextElement()
                if (!adress.isLoopbackAddress && !adress.isLinkLocalAddress && adress.isSiteLocalAddress) {
                    return adress.hostAddress!!
                }
            }
        }
        error("Could find ip adress")
    }
}
