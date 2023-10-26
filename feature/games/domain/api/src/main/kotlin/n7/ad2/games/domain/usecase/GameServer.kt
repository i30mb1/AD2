package n7.ad2.games.domain.usecase

interface GameServer {
    companion object {
        const val host = "192.168.100.8"
        val ports = intArrayOf(50088, 50001)
    }

    fun runServer()
}
