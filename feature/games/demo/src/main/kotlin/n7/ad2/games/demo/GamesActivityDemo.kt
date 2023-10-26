package n7.ad2.games.demo

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.graphics.asImageBitmap
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import n7.ad2.game.demo.R
import n7.ad2.games.domain.usecase.GameServer
import n7.ad2.nativesecret.NativeSecretExtractor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class GamesActivityDemo(
    private val logger: (message: String) -> Unit,
) : FragmentActivity() {

//    private val server = GameServer(logger)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extractor = NativeSecretExtractor()
        val text = extractor.printHelloWorld()
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.img800x450b)
        val resizeBitmap = extractor.resize(bitmap, 450, 450)
        setContent {
            Column {
                Button(onClick = ::runServer) {
                    Text(text = "Run Server $text")
                }
                Button(onClick = ::sendRequest) {
                    Text(text = "Send Request")
                }
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "some useful description",
                )
                Image(
                    bitmap = resizeBitmap.asImageBitmap(),
                    contentDescription = "some useful description",
                )
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
//        server.run()
    }
}
