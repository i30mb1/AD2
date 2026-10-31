package n7.ad2.games.internal.party

import n7.ad2.feature.games.xo.domain.model.WifiDirectRoom
import n7.ad2.games.room.GameMode

/** Выбор игры у обоих; [match] - одинаковый выбор, пора стартовать */
internal data class PartyPicks(val mine: Pick? = null, val peer: Pick? = null) {
    val match: Pick? get() = mine?.takeIf { it == peer }

    fun pick(pick: Pick) = copy(mine = pick)

    fun cancel() = copy(mine = null)

    fun peerPicked(pick: Pick?) = copy(peer = pick)
}

internal object PartyCodec {

    // id в конце имени сервиса: имя игрока может содержать что угодно, id - нет
    private const val ID_SEPARATOR = "#"

    /** Выбор второго игрока, [pick] null - передумал */
    data class Picked(val pick: Pick?)

    fun encodePick(pick: Pick?): String = pick?.let { "${it.gameId};${it.mode.name}" }.orEmpty()

    fun decodePick(payload: String): Picked? {
        if (payload.isEmpty()) return Picked(null)
        val parts = payload.split(";")
        if (parts.size != 2) return null
        val mode = GameMode.entries.firstOrNull { it.name == parts[1] } ?: return null
        return Picked(Pick(parts[0], mode))
    }

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
