package n7.ad2.games.demo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import n7.ad2.games.demo.server.GameServer
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class GamesActivityDemo(
    private val logger: (message: String) -> Unit,
) : FragmentActivity() {

    private val server = GameServer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Button(onClick = ::runServer) {
                    Text(text = "Run Server")
                }
                Button(onClick = ::sendRequest) {
                    Text(text = "Send Request")
                }
            }
        }
    }

    private fun sendRequest() {
        lifecycleScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("ws://${GameServer.host}:${GameServer.ports.first()}")
                .build()
            OkHttpClient().newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    logger("success")
                }

                override fun onFailure(webSocket: WebSocket, throwable: Throwable, response: Response?) {
                    logger("failure:$throwable")
                }
            })
        }
    }

    private fun runServer() {
        server.run()
    }
}
