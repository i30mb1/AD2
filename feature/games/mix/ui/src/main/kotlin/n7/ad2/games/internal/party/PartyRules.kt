package n7.ad2.games.internal.party

import androidx.compose.runtime.Immutable
import n7.ad2.feature.games.xo.domain.model.WifiDirectRoom
import n7.ad2.games.internal.data.maxPlayersOf
import n7.ad2.games.internal.data.modesOf
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Player

/**
 * Игра комнаты и кто к ней готов. Игру и режим выбирает хост, гости жмут «Готов»; новый выбор хоста снимает готовность
 * у всех. Хост всегда готов, [ready] - id готовых гостей.
 */
@Immutable
internal data class Lobby(val gameId: String? = null, val mode: GameMode = GameMode.VERSUS, val ready: Set<String> = emptySet()) {

    fun pick(gameId: String, mode: GameMode): Lobby = if (gameId == this.gameId && mode == this.mode) this else Lobby(gameId, mode)

    fun ready(playerId: String, isReady: Boolean): Lobby = copy(ready = if (isReady) ready + playerId else ready - playerId)

    /** [players] - хост первый */
    fun isReady(playerId: String, players: List<Player>): Boolean = playerId == players.firstOrNull()?.id || playerId in ready

    fun readyCount(players: List<Player>): Int = players.count { isReady(it.id, players) }

    /** Игра выбрана, подходит всем в комнате по числу игроков и режиму, и все готовы */
    fun canStart(players: List<Player>): Boolean {
        val gameId = gameId ?: return false
        return players.size in 2..maxPlayersOf(gameId) && mode in modesOf(gameId) && readyCount(players) == players.size
    }

    /** После игры каждый снова жмет «Готов» */
    fun reset(): Lobby = copy(ready = emptySet())

    /** Ушедшие не готовы */
    fun forPlayers(players: List<Player>): Lobby = copy(ready = ready.filterTo(mutableSetOf()) { id -> players.any { it.id == id } })
}

/** Лобби от хоста всем: "l;gameId;MODE;id,id"; готовность гостя хосту: "r;1" или "r;0" */
internal object LobbyCodec {

    sealed interface Message {
        data class State(val lobby: Lobby) : Message
        data class Ready(val ready: Boolean) : Message
    }

    fun encode(lobby: Lobby): String = listOf(STATE, lobby.gameId.orEmpty(), lobby.mode.name, lobby.ready.sorted().joinToString(",")).joinToString(";")

    fun encodeReady(ready: Boolean): String = "$READY;${if (ready) 1 else 0}"

    fun decode(payload: String): Message? {
        val parts = payload.split(";")
        return when {
            parts.size == 4 && parts[0] == STATE -> {
                val mode = GameMode.entries.firstOrNull { it.name == parts[2] } ?: return null
                Message.State(Lobby(parts[1].ifEmpty { null }, mode, parts[3].split(",").filter { it.isNotEmpty() }.toSet()))
            }
            parts.size == 2 && parts[0] == READY -> Message.Ready(parts[1] == "1")
            else -> null
        }
    }

    private const val STATE = "l"
    private const val READY = "r"
}

internal object PartyCodec {

    // id в конце имени сервиса: имя игрока может содержать что угодно, id - нет
    private const val ID_SEPARATOR = "#"

    /** Имя NSD-сервиса: по нему отличаем игроков от чужих сервисов того же типа (XO) */
    fun presenceName(name: String, id: String): String = "$name$ID_SEPARATOR$id"

    // NSD при конфликте имен дописывает " (2)": например, своя прошлая регистрация еще не снята
    private val CONFLICT_SUFFIX = Regex(""" \(\d+\)$""")

    /** @return имя и id игрока или null, если сервис не наш */
    fun parsePresence(rawServiceName: String): Pair<String, String>? {
        val serviceName = rawServiceName.replace(CONFLICT_SUFFIX, "")
        val separator = serviceName.lastIndexOf(ID_SEPARATOR)
        if (separator <= 0 || separator == serviceName.lastIndex) return null
        return serviceName.substring(0, separator) to serviceName.substring(separator + 1)
    }
}

/** Игроки рядом без себя; один игрок по Wi-Fi и Wi-Fi Direct - одна строка, Wi-Fi надежнее */
internal fun mergeNearby(lan: List<LanPresence>, direct: List<WifiDirectRoom>, myId: String): List<NearbyPlayer> {
    val inWifi = lan.mapNotNull { service ->
        val (name, id) = PartyCodec.parsePresence(service.serviceName) ?: return@mapNotNull null
        NearbyPlayer(id, name, Via.WIFI, service.ip, service.port, deviceAddress = null)
    }
    val nearby = direct.map { room -> NearbyPlayer(room.id, room.name, Via.DIRECT, ip = null, room.port, room.deviceAddress) }
    return (inWifi + nearby).filter { it.id != myId }.distinctBy { it.id }
}

/** Оба одновременно вошли друг к другу: у каждого две комнаты, оба оставляют ту, где хост - больший id */
internal fun isPreferredHost(hostId: String, myId: String, peerId: String): Boolean = hostId == maxOf(myId, peerId)
