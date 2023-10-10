package n7.ad2.games.demo.server

import java.io.InputStream
import java.net.NetworkInterface

internal fun getIPAddress(): String {
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

internal fun InputStream.getBytes(size: Int): ByteArray {
    return ByteArray(size).apply { read(this) }
}

internal fun InputStream.getBytes(size: Long): ByteArray {
    return getBytes(size.toInt())
}
