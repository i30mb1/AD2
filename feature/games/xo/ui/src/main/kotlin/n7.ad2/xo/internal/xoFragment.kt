package n7.ad2.xo.internal

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
import java.net.InetAddress
import javax.inject.Inject
import n7.ad2.android.DependenciesMap
import n7.ad2.android.HasDependencies
import n7.ad2.android.findDependencies
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.games.xo.domain.Client
import n7.ad2.feature.games.xo.domain.Server
import n7.ad2.ktx.viewModel
import n7.ad2.ui.ComposeView
import n7.ad2.xo.internal.di.DaggerXoComponent

internal class XoFragment(
    override var dependenciesMap: DependenciesMap,
) : Fragment(), HasDependencies {

    @Inject lateinit var skillGameViewModelFactory: XoViewModel.Factory
    private val viewModel: XoViewModel by viewModel { skillGameViewModelFactory.create() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerXoComponent.factory().create(findDependencies()).inject(this)
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
                Text(text = viewModel.text, color = Color.White)
            }
        }
    }

    private fun runClient() {
        viewModel.runClient()
    }

    private fun sendPong() {
        viewModel.sendPong()
    }

    private fun sendPingToClient() {
        viewModel.sendPing()
    }

    private fun runServer() {
        viewModel.runServer()
    }
}

