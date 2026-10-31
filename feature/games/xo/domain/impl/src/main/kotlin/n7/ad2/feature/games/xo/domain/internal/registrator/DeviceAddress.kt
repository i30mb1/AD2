package n7.ad2.feature.games.xo.domain.internal.registrator

import java.net.Inet4Address
import java.net.InetAddress

/**
 * Адрес, на котором стоит поднимать сервер: IPv4, иначе не link-local адрес, иначе любой.
 * link-local IPv6 (fe80::) недоступен другим устройствам без указания интерфейса.
 */
internal fun pickDeviceAddress(addresses: List<InetAddress>): InetAddress? = addresses.firstOrNull { it is Inet4Address && !it.isLoopbackAddress }
    ?: addresses.firstOrNull { !it.isLinkLocalAddress && !it.isLoopbackAddress }
    ?: addresses.firstOrNull()
