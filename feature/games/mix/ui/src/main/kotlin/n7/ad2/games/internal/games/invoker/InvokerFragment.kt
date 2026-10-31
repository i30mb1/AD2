package n7.ad2.games.internal.games.invoker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import javax.inject.Inject
import kotlinx.coroutines.launch
import n7.ad2.android.findDependencies
import n7.ad2.core.ui.content
import n7.ad2.games.internal.di.DaggerGamesComponent
import n7.ad2.games.internal.games.invoker.compose.InvokerActions
import n7.ad2.games.internal.games.invoker.compose.InvokerScreen
import n7.ad2.games.room.GameMode
import n7.ad2.ktx.viewModel

internal class InvokerFragment : Fragment() {

    companion object {
        private const val ARG_MODE = "mode"

        fun getInstance(mode: GameMode = GameMode.SOLO) = InvokerFragment().apply { arguments = bundleOf(ARG_MODE to mode.name) }
    }

    @Inject lateinit var invokerViewModelFactory: InvokerViewModel.Factory
    private val viewModel: InvokerViewModel by viewModel {
        invokerViewModelFactory.create(GameMode.valueOf(requireArguments().getString(ARG_MODE, GameMode.SOLO.name)))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerGamesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = content {
        InvokerScreen(
            state = viewModel.state.collectAsState().value,
            elapsedMs = viewModel::elapsedMs,
            countdown = viewModel::countdown,
            actions = InvokerActions(
                onOrb = viewModel::press,
                onInvoke = viewModel::invoke,
                onHint = viewModel::hint,
                onAgain = viewModel::again,
                onOtherGame = viewModel::otherGame,
            ),
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.exit.collect { parentFragmentManager.popBackStack() }
            }
        }
    }
}
