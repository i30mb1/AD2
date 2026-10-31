package n7.ad2.games.internal.games.killCreep

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
import kotlinx.coroutines.launch
import n7.ad2.android.findDependencies
import n7.ad2.core.ui.content
import n7.ad2.games.internal.di.DaggerGamesComponent
import n7.ad2.games.internal.games.killCreep.compose.KillCreepScreen
import n7.ad2.games.room.GameMode
import n7.ad2.ktx.viewModel
import javax.inject.Inject

internal class KillCreepFragment : Fragment() {

    companion object {
        private const val ARG_MODE = "mode"

        fun getInstance(mode: GameMode = GameMode.SOLO) = KillCreepFragment().apply { arguments = bundleOf(ARG_MODE to mode.name) }
    }

    @Inject lateinit var killCreepViewModelFactory: KillCreepViewModel.Factory
    private val viewModel: KillCreepViewModel by viewModel {
        killCreepViewModelFactory.create(GameMode.valueOf(requireArguments().getString(ARG_MODE, GameMode.SOLO.name)))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerGamesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = content {
        KillCreepScreen(viewModel.state.collectAsState().value, viewModel.sim, viewModel::elapsedMs, viewModel::countdown, viewModel::onTap, viewModel::again, viewModel::otherGame)
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
