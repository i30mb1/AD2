package n7.ad2.games.internal.games.show

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
import n7.ad2.games.internal.games.show.compose.ShowActions
import n7.ad2.games.internal.games.show.compose.ShowScreen
import n7.ad2.games.room.GameMode
import n7.ad2.ktx.viewModel

internal class ShowFragment : Fragment() {

    companion object {
        private const val ARG_MODE = "mode"

        fun getInstance(mode: GameMode = GameMode.SOLO) = ShowFragment().apply { arguments = bundleOf(ARG_MODE to mode.name) }
    }

    @Inject lateinit var showViewModelFactory: ShowViewModel.Factory
    private val viewModel: ShowViewModel by viewModel {
        showViewModelFactory.create(GameMode.valueOf(requireArguments().getString(ARG_MODE, GameMode.SOLO.name)))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerGamesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = content {
        ShowScreen(
            state = viewModel.state.collectAsState().value,
            actions = ShowActions(
                onBet = viewModel::toggleBet,
                onPick = viewModel::pick,
                onTile = viewModel::toggleTile,
                onBuild = viewModel::build,
                onBuzz = viewModel::buzz,
                onFifty = viewModel::fiftyFifty,
                onFreeze = viewModel::freeze,
                onReplay = viewModel::replayVoice,
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
