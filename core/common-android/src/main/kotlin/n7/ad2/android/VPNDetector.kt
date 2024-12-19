package n7.ad2.android

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.telephony.TelephonyManager
import androidx.core.content.getSystemService
import java.net.NetworkInterface

@SuppressLint("MissingPermission")
class VPNDetector(
    private val context: Context,
) {

    private val connectivityManager by lazy { context.getSystemService<ConnectivityManager>()!! }
    private val telephonyManager by lazy { context.getSystemService<TelephonyManager>()!! }

    fun isnEnabled(): Boolean {
        return isEnabledViaConnectivityManager() || isEnabledViaNetworkInterface()
    }

    private fun isEnabledViaConnectivityManager(): Boolean {
        try {
            val activeNetwork = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ?: false
        } catch (_: Exception) {
            return false
        }
    }

    private fun isEnabledViaNetworkInterface(): Boolean {
        return try {
            NetworkInterface.getNetworkInterfaces().asSequence()
                .filter { it.isUp }
                .any { networkInterface ->
                    VPN_INTERFACE_KEYWORDS.any { vpnKeyword -> networkInterface.name.contains(vpnKeyword) }
                }
        } catch (_: Exception) {
            false
        }
    }

    fun getConnectionType(): String {
        try {
            val activeNetwork: Network = connectivityManager.activeNetwork ?: return "Unknown"
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return "Unknown"
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Wired Ethernet"
                else -> "Unknown"
            }
        } catch (_: Exception) {
            return "Unknown"
        }
    }

    fun getCellularType(): String {
        return try {
            when (telephonyManager.dataNetworkType) {
                TelephonyManager.NETWORK_TYPE_GPRS -> "2G"
                TelephonyManager.NETWORK_TYPE_EDGE -> "2G"
                TelephonyManager.NETWORK_TYPE_CDMA -> "2G"
                TelephonyManager.NETWORK_TYPE_EVDO_0 -> "3G"
                TelephonyManager.NETWORK_TYPE_EVDO_A -> "3G"
                TelephonyManager.NETWORK_TYPE_EVDO_B -> "3G"
                TelephonyManager.NETWORK_TYPE_1xRTT -> "2G"
                TelephonyManager.NETWORK_TYPE_HSDPA -> "3G"
                TelephonyManager.NETWORK_TYPE_HSUPA -> "3G"
                TelephonyManager.NETWORK_TYPE_HSPA -> "3G"
                TelephonyManager.NETWORK_TYPE_LTE -> "4G"
                TelephonyManager.NETWORK_TYPE_NR -> "5G"
                else -> "Unknown"
            }
        } catch (_: Exception) {
            "Unknown"
        }
    }

    private companion object {
        val VPN_INTERFACE_KEYWORDS = listOf("tun", "ppp", "pptp")
    }
}
