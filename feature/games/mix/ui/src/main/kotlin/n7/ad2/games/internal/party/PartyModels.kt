package n7.ad2.games.internal.party

import androidx.compose.runtime.Immutable
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Player

internal enum class Via { WIFI, DIRECT }

/** Игрок рядом: по Wi-Fi известен ip, по Wi-Fi Direct - адрес устройства (ip станет известен после входа в группу) */
@Immutable
internal data class NearbyPlayer(val id: String, val name: String, val via: Via, val ip: String?, val port: Int, val deviceAddress: String?)

/** Сервис, найденный в Wi-Fi сети (NSD), как есть */
internal data class LanPresence(val serviceName: String, val ip: String, val port: Int)

/** Выбор игры: одинаковый у обоих - игра стартует */
@Immutable
internal data class Pick(val gameId: String, val mode: GameMode)

/** [via] null - второй игрок подключился к нам сам, транспорт неизвестен */
@Immutable
internal data class Session(val peer: Player, val via: Via?)

/**
 * @param connecting после play(), пока не подключились или не упали
 * @param myAddress "ip:port" своей комнаты для ручного подключения, null без Wi-Fi
 * @param wifiDirect нас видно по Wi-Fi Direct
 */
@Immutable
internal data class PartyState(
    val nearby: List<NearbyPlayer> = emptyList(),
    val session: Session? = null,
    val myPick: Pick? = null,
    val peerPick: Pick? = null,
    val connecting: Boolean = false,
    val failed: Boolean = false,
    val myAddress: String? = null,
    val wifiDirect: Boolean = false,
)
