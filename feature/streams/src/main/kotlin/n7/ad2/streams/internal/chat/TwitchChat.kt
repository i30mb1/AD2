package n7.ad2.streams.internal.chat

import dagger.Lazy
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import kotlin.random.Random

private const val CHAT_URL = "wss://irc-ws.chat.twitch.tv:443"
private const val NORMAL_CLOSURE = 1000

/**
 * Reads the chat of a channel without an account: Twitch lets anyone in as "justinfan<number>", such a user can only read.
 * The flow fails when the connection drops, collect it with a retry.
 */
// the client is lazy: it must not be built on the main thread, where the player screen is injected
internal class TwitchChat @Inject constructor(private val okHttpClient: Lazy<OkHttpClient>) {

    fun messages(channel: String): Flow<ChatMessage> = callbackFlow {
        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // the tags carry the author's color and the name as they write it
                webSocket.send("CAP REQ :twitch.tv/tags")
                webSocket.send("PASS SCHMOOPIIE")
                webSocket.send("NICK justinfan${Random.nextInt(10_000, 99_999)}")
                webSocket.send("JOIN #${channel.lowercase()}")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                text.lineSequence().filter { it.isNotBlank() }.forEach { line ->
                    // Twitch closes the connection when a ping is left without an answer
                    if (line.startsWith("PING")) {
                        webSocket.send(line.replaceFirst("PING", "PONG"))
                    } else {
                        TwitchChatParser.parse(line)?.let(::trySend)
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                close(t)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                close(IllegalStateException("chat closed: $code $reason"))
            }
        }
        val socket = okHttpClient.get().newWebSocket(Request.Builder().url(CHAT_URL).build(), listener)
        awaitClose { socket.close(NORMAL_CLOSURE, null) }
    }
}
