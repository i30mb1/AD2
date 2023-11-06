package n7.ad2.games.internal.games.xo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import java.net.InetAddress
import javax.inject.Inject
import kotlinx.coroutines.launch
import n7.ad2.android.DependenciesMap
import n7.ad2.android.HasDependencies
import n7.ad2.android.findDependencies
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.games.domain.Client
import n7.ad2.games.domain.Server
import n7.ad2.games.internal.di.DaggerGamesComponent
import n7.ad2.ktx.viewModel
import n7.ad2.nativesecret.NativeSecretExtractor
import n7.ad2.ui.ComposeView

internal class XOGameFragment(
    override var dependenciesMap: DependenciesMap,
) : Fragment(), HasDependencies {

    private val ip = InetAddress.getByName("192.168.100.29")
    private val dispatchers = DispatchersProvider()
    @Inject lateinit var server: Server
    @Inject lateinit var client: Client
    private var text by mutableStateOf("")

    @Inject lateinit var skillGameViewModelFactory: XOGameViewModel.Factory
    private val viewModel: XOGameViewModel by viewModel { skillGameViewModelFactory.create() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerGamesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row {
                    Button(onClick = ::runServer) {
                        Text(text = "run server")
                    }
                    Button(onClick = ::sendPingToClient) {
                        Text(text = "send ping to client")
                    }
                }
                Row {
                    Button(onClick = ::runClient) {
                        Text(text = "connect to server")
                    }
                    Button(onClick = ::sendPong) {
                        Text(text = "send pong to server")
                    }
                }
                Text(text = text, color = Color.White)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text = NativeSecretExtractor().printHelloWorld()
    }

    private fun runClient() = lifecycleScope.launch(dispatchers.IO) {
        client.start(ip, 8080)
        text = "Connected to Server\n"
        while (true) {
            val message = client.awaitMessage()
            text += "$message\n"
        }
    }

    private fun sendPong() = lifecycleScope.launch(dispatchers.IO) {
        client.sendMessage("pong")
    }

    private fun sendPingToClient() = lifecycleScope.launch(dispatchers.IO) {
        server.sendMessage("ping")
    }

    private fun runServer() = lifecycleScope.launch(dispatchers.IO) {
        server.start(ip, intArrayOf(8080))
        server.awaitClient()
        text = "Client Connected\n"
        while (true) {
            val message = server.awaitMessage()
            text += "$message\n"
        }
    }
}
