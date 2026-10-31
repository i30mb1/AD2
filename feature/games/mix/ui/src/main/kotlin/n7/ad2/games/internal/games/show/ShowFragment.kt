package n7.ad2.games.internal.games.show

import android.content.Context
import android.content.Intent
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
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.launch
import n7.ad2.android.findDependencies
import n7.ad2.core.ui.content
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.di.DaggerGamesComponent
import n7.ad2.games.internal.games.show.compose.ShowActions
import n7.ad2.games.internal.games.show.compose.ShowScreen
import n7.ad2.games.room.GameMode
import n7.ad2.ktx.viewModel

internal class ShowFragment : Fragment() {

    companion object {
        private const val ARG_MODE = "mode"
        private const val ARG_DAILY = "daily"

        fun getInstance(mode: GameMode = GameMode.SOLO, daily: Boolean = false) = ShowFragment().apply { arguments = bundleOf(ARG_MODE to mode.name, ARG_DAILY to daily) }
    }

    @Inject lateinit var showViewModelFactory: ShowViewModel.Factory
    private val viewModel: ShowViewModel by viewModel {
        showViewModelFactory.create(GameMode.valueOf(requireArguments().getString(ARG_MODE, GameMode.SOLO.name)), requireArguments().getBoolean(ARG_DAILY))
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
                onShare = ::share,
            ),
        )
    }

    /** The result of the day goes to any chat as text with squares */
    private fun share() {
        val date = viewModel.state.value.date.format(DateTimeFormatter.ofPattern("d MMMM", resources.configuration.locales[0]))
        val text = viewModel.shareText(getString(R.string.daily_share_title, date))
        val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(send, null))
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
