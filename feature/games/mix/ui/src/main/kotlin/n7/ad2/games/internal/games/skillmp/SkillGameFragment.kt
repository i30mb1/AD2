package n7.ad2.games.internal.games.skillmp

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
import n7.ad2.games.internal.games.skillmp.compose.ManaPointScreen
import n7.ad2.games.room.GameMode
import n7.ad2.ktx.viewModel
import javax.inject.Inject

internal class SkillGameFragment : Fragment() {

    companion object {
        private const val ARG_MODE = "mode"
        private const val ARG_GAME = "game"

        fun getInstance(mode: GameMode = GameMode.SOLO, gameId: String = SkillGameViewModel.GAME_ID) =
            SkillGameFragment().apply { arguments = bundleOf(ARG_MODE to mode.name, ARG_GAME to gameId) }
    }

    @Inject lateinit var skillGameViewModelFactory: SkillGameViewModel.Factory
    private val viewModel: SkillGameViewModel by viewModel {
        skillGameViewModelFactory.create(
            GameMode.valueOf(requireArguments().getString(ARG_MODE, GameMode.SOLO.name)),
            requireArguments().getString(ARG_GAME, SkillGameViewModel.GAME_ID),
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerGamesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = content {
        val state = viewModel.state.collectAsState().value
        ManaPointScreen(state = state, onVariantClick = viewModel::onAnswer, onToggleTile = viewModel::onToggleTile, onBuild = viewModel::onBuild, onAgain = viewModel::again, onOtherGame = viewModel::otherGame)
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
