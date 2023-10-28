package n7.ad2.games.domain.internal.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.games.domain.usecase.GameServer

internal class GameServerImpl(
    dispatchers: DispatchersProvider,
) : GameServer {

    private val scope = CoroutineScope(Job() + dispatchers.IO)
    private val incomingMessages = Channel<String>()
//    private val server = Server()

    override fun runServer() {
//        server.run()
    }

    suspend fun handleIncommingMessage() {
        incomingMessages.send("")

        if (false) {
            incomingMessages.close()
        }
    }

    suspend fun sendMessage(myMove: String) {
        // socketSend
    }

    suspend fun awaitOpponent(): String {
        return incomingMessages.receive()
    }

    suspend fun playGame() {
        while (true) {
            val myMove = ""
            sendMessage(myMove)

            val opponentMove = awaitOpponent()
            processOpponentMove(opponentMove)
        }
    }

    private suspend fun processOpponentMove(opponentMove: String) {

    }
}
