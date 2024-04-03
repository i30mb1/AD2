package n7.ad2.games.internal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import javax.inject.Inject
import n7.ad2.android.DependenciesMap
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.HasDependencies
import n7.ad2.android.findDependencies
import n7.ad2.android.getMainFragmentNavigator
import n7.ad2.app.logger.Logger
import n7.ad2.games.internal.compose.GamesScreen
import n7.ad2.games.internal.data.GameVO
import n7.ad2.games.internal.di.DaggerGamesComponent
import n7.ad2.games.internal.games.killCreep.KillCreepFragment
import n7.ad2.games.internal.games.skillmp.SkillGameFragment
import n7.ad2.ktx.viewModel
import n7.ad2.ui.ComposeView

internal class GamesFragment(
    override var dependenciesMap: DependenciesMap,
) : Fragment(), HasDependencies {

    @Inject lateinit var gamesViewModelFactory: GamesViewModel.Factory
    @Inject lateinit var logger: Logger

    private val viewModel: GamesViewModel by viewModel { gamesViewModelFactory.create() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerGamesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView {
            GamesScreen(viewModel, parentFragment as? DrawerPercentListener, ::onGameClicked)
        }
    }

    private fun onGameClicked(game: GameVO) {
        val fragment = when (game) {
            is GameVO.Apm -> KillCreepFragment.getInstance()
            is GameVO.CanYouBuyIt -> TODO()
            is GameVO.GuessSkillMana -> SkillGameFragment.getInstance()
            else -> TODO()
        }
        getMainFragmentNavigator?.setMainFragment(fragment) {
            addToBackStack(null)
        }
    }
}
