package n7.ad2.games.internal.party

import androidx.compose.runtime.Immutable
import n7.ad2.games.room.CloseReason
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Player

internal enum class Via { WIFI, DIRECT }

/** Игрок рядом: по Wi-Fi известен ip, по Wi-Fi Direct - адрес устройства (ip станет известен после входа в группу) */
@Immutable
internal data class NearbyPlayer(val id: String, val name: String, val via: Via, val ip: String?, val port: Int, val deviceAddress: String?)

/** Сервис, найденный в Wi-Fi сети (NSD), как есть */
internal data class LanPresence(val serviceName: String, val ip: String, val port: Int)

/** Игра и режим: «Ещё раз» с экрана итогов выбирает их снова */
@Immutable
internal data class Pick(val gameId: String, val mode: GameMode)

/**
 * Комната с другими игроками: [players] - хост первый, гости в порядке входа; [lobby] - игра, которую выбрал хост,
 * и кто к ней готов; [via] null - к нам вошли сами, транспорт неизвестен.
 */
@Immutable
internal data class Session(val players: List<Player>, val meId: String, val lobby: Lobby, val via: Via?) {
    val host: Player get() = players.first()
    val isHost: Boolean get() = host.id == meId
    val meReady: Boolean get() = lobby.isReady(meId, players)
}

/** Почему не удалось войти в комнату */
internal enum class PartyFailure {
    CONNECTION, FULL, BUSY, VERSION;

    companion object {
        fun of(reason: CloseReason): PartyFailure = when (reason) {
            CloseReason.FULL -> FULL
            CloseReason.BUSY -> BUSY
            CloseReason.VERSION_MISMATCH -> VERSION
            else -> CONNECTION
        }
    }
}

/**
 * @param connecting после play(), пока не вошли или не упали
 * @param myAddress "ip:port" своей комнаты для ручного подключения, null без Wi-Fi
 * @param wifiDirect нас видно по Wi-Fi Direct
 */
@Immutable
internal data class PartyState(
    val nearby: List<NearbyPlayer> = emptyList(),
    val session: Session? = null,
    val connecting: Boolean = false,
    val failure: PartyFailure? = null,
    val myAddress: String? = null,
    val wifiDirect: Boolean = false,
)
