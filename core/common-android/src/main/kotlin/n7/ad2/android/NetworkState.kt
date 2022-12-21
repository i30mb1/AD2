package n7.ad2.android

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NetworkState(
    context: Context,
) {

    sealed class Status {
        object Unavailable : Status()
        object Available : Status()
    }

    private val connectivityManager: ConnectivityManager = context.getSystemService()!!
    @SuppressLint("MissingPermission") val networkState: Flow<Status> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(Status.Available)
            }

            override fun onUnavailable() {
                trySend(Status.Unavailable)
            }

            override fun onLost(network: Network) {
                trySend(Status.Unavailable)
            }
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
        awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
    }

}