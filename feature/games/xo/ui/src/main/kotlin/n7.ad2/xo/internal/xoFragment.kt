package n7.ad2.xo.internal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.Fragment
import javax.inject.Inject
import n7.ad2.android.DependenciesMap
import n7.ad2.android.HasDependencies
import n7.ad2.android.findDependencies
import n7.ad2.ktx.viewModel
import n7.ad2.ui.ComposeView
import n7.ad2.xo.internal.compose.XoScreen
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
            val state = viewModel.state.collectAsState().value
            XoScreen(state = state)
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
