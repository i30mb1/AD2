package n7.ad2.feature.games.xo.domain.model

class Server(
    val name: String,
    val serverIP: String,
    val port: Int,
    val isWifiDirect: Boolean = false,
)
