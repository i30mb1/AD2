package n7.ad2.feature.games.xo.domain.internal.registrator

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import java.net.InetAddress
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import n7.ad2.feature.games.xo.domain.GetNetworkStateUseCase
import n7.ad2.feature.games.xo.domain.model.NetworkState

internal class GetNetworkStateUseCaseImpl(
    context: Context,
) : GetNetworkStateUseCase {

    private val connectivityManager: ConnectivityManager by lazy { context.getSystemService(ConnectivityManager::class.java)!! }

    override fun invoke(): Flow<NetworkState> = callbackFlow {
        val networkStateCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                val inetAddress = getInetAddress()
                val ip = requireNotNull(inetAddress?.hostAddress) { "inetAddress?.hostAddress is null" }
                trySend(NetworkState.Connected(ip))
            }

            override fun onLost(network: android.net.Network) {
                trySend(NetworkState.Disconnected)
            }
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkStateCallback)
        awaitClose { connectivityManager.unregisterNetworkCallback(networkStateCallback) }
    }
        .onStart { emit(NetworkState.Disconnected) }

    private fun getInetAddress(): InetAddress? {
        val properties = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)
        return properties?.linkAddresses
            ?.first { adress -> adress.address.isSiteLocalAddress }
            ?.address
    }
}
