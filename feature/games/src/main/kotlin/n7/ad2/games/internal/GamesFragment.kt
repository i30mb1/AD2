package n7.ad2.games.internal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.findDependencies
import n7.ad2.android.getNavigator
import n7.ad2.games.internal.compose.GamesScreen
import n7.ad2.games.internal.data.Players
import n7.ad2.games.internal.di.DaggerGamesComponent
import n7.ad2.games.internal.games.SkillGameFragment
import n7.ad2.ktx.viewModel
import n7.ad2.logger.Logger
import n7.ad2.ui.ComposeView
import javax.inject.Inject

internal class GamesFragment : Fragment() {

    companion object {
        fun getInstance() = GamesFragment()
    }

    @Inject lateinit var gamesViewModelFactory: GamesViewModel.Factory
    @Inject lateinit var logger: Logger

    private val viewModel: GamesViewModel by viewModel { gamesViewModelFactory.create() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerGamesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView { GamesScreen(viewModel, parentFragment as DrawerPercentListener, ::onGameClicked) }
    }

    private fun onGameClicked(players: Players) {
        getNavigator.setMainFragment(SkillGameFragment.getInstance()) {
            addToBackStack(null)
        }
    }

}