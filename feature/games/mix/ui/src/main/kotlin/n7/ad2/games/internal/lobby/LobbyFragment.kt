package n7.ad2.games.internal.lobby

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import n7.ad2.android.findDependencies
import n7.ad2.android.getMainFragmentNavigator
import n7.ad2.core.ui.content
import n7.ad2.games.internal.di.DaggerGamesComponent
import n7.ad2.games.internal.games.killCreep.KillCreepFragment
import n7.ad2.games.internal.games.killCreep.KillCreepViewModel
import n7.ad2.games.internal.games.skillmp.SkillGameFragment
import n7.ad2.games.internal.games.skillmp.SkillGameViewModel
import n7.ad2.games.room.GameMode
import n7.ad2.ktx.viewModel
import javax.inject.Inject

internal class LobbyFragment : Fragment() {

    companion object {
        private const val ARG_MODE = "mode"
        private const val ARG_GAME = "game"

        fun getInstance(mode: GameMode, gameId: String) = LobbyFragment().apply { arguments = bundleOf(ARG_MODE to mode.name, ARG_GAME to gameId) }
    }

    @Inject lateinit var lobbyViewModelFactory: LobbyViewModel.Factory
    private val mode by lazy { GameMode.valueOf(requireArguments().getString(ARG_MODE, GameMode.VERSUS.name)) }
    private val viewModel: LobbyViewModel by viewModel { lobbyViewModelFactory.create(mode, requireArguments().getString(ARG_GAME, SkillGameViewModel.GAME_ID)) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerGamesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = content {
        LobbyScreen(
            state = viewModel.state.collectAsState().value,
            mode = mode,
            onTab = viewModel::selectTab,
            onJoin = viewModel::join,
            onJoinManual = viewModel::joinManual,
            onStart = viewModel::start,
            onRecreate = viewModel::recreate,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.openGame.collect { playing ->
                // лобби больше не нужно: назад из игры ведет в список игр
                parentFragmentManager.popBackStack()
                val game = if (playing.gameId == KillCreepViewModel.GAME_ID) {
                    KillCreepFragment.getInstance(playing.mode)
                } else {
                    SkillGameFragment.getInstance(playing.mode, playing.gameId)
                }
                getMainFragmentNavigator?.setMainFragment(game) { addToBackStack(null) }
            }
        }
    }
}
