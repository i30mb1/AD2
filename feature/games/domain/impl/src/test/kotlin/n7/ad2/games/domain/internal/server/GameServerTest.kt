package n7.ad2.games.domain.internal.server

import kotlinx.coroutines.test.runTest
import org.junit.Test


class GameServerTest {

    private val server = GameServer { }

    @Test
    fun testConnection() = runTest {
        server.run2()
    }
}
