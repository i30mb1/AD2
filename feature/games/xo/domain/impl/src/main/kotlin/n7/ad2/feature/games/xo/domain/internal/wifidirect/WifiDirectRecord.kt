package n7.ad2.feature.games.xo.domain.internal.wifidirect

import n7.ad2.feature.games.xo.domain.model.WifiDirectRoom

/**
 * TXT запись объявления комнаты в Wi-Fi Direct. Метка app отсекает чужие сервисы того же типа
 */
internal object WifiDirectRecord {

    // запись TXT - до 255 байт на "ключ=значение", имя в UTF-8 бывает по 2 байта на символ
    const val MAX_NAME = 100

    private const val APP = "ad2"

    fun encode(name: String, port: Int): Map<String, String> = mapOf("app" to APP, "name" to name.take(MAX_NAME), "port" to port.toString())

    fun decode(record: Map<String, String>, deviceAddress: String): WifiDirectRoom? {
        if (record["app"] != APP) return null
        val port = record["port"]?.toIntOrNull()?.takeIf { it in 1..65535 } ?: return null
        val name = record["name"].orEmpty().ifBlank { deviceAddress }
        return WifiDirectRoom(name, deviceAddress, port)
    }
}
