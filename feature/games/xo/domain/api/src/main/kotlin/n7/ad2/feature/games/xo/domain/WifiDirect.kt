package n7.ad2.feature.games.xo.domain

import kotlinx.coroutines.flow.Flow
import n7.ad2.feature.games.xo.domain.model.WifiDirectRoom
import java.net.InetAddress

/**
 * Комнаты без общей Wi-Fi сети: хост поднимает группу Wi-Fi Direct и объявляет в ней порт своего сервера,
 * гость находит объявление, входит в группу и подключается к серверу по адресу владельца группы.
 * Все вызовы требуют NEARBY_WIFI_DEVICES (до Android 13 - ACCESS_FINE_LOCATION), без него молча ничего не делают.
 */
interface WifiDirect {
    /** Хост: создать группу (мы ее владелец) и объявить комнату [name] на [port] */
    suspend fun advertise(name: String, port: Int)

    /** Гость: комнаты, объявленные рядом */
    fun discover(): Flow<List<WifiDirectRoom>>

    /** Гость: войти в группу хоста, вернуть адрес хоста в ней или null, если не вышло */
    suspend fun connect(deviceAddress: String): InetAddress?

    /** Снять объявление и выйти из группы (и хосту, и гостю) */
    fun release()
}
