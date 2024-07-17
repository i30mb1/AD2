package n7.ad2.feature.games.xo.domain.model

interface Server {
    val name: String
    val serverIP: String
    val port: Int
}

class SimpleServer(
    override val name: String,
    override val serverIP: String,
    override val port: Int,
) : Server