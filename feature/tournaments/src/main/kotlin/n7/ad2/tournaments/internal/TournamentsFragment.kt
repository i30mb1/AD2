package n7.ad2.tournaments.internal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.Fragment
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.findDependencies
import n7.ad2.android.getMainFragmentNavigator
import n7.ad2.core.ui.content
import n7.ad2.ktx.viewModel
import n7.ad2.tournaments.internal.data.TournamentMatch
import n7.ad2.tournaments.internal.di.DaggerTournamentsComponent
import n7.ad2.tournaments.internal.match.MatchFragment
import javax.inject.Inject

class TournamentsFragment : Fragment() {

    @Inject lateinit var tournamentsViewModelFactory: TournamentsViewModel.Factory

    private val viewModel: TournamentsViewModel by viewModel { tournamentsViewModelFactory.create() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerTournamentsComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = content {
        val state = viewModel.state.collectAsState().value
        TournamentsScreen(state, parentFragment as DrawerPercentListener, ::onMatchClicked, viewModel::load)
    }

    private fun onMatchClicked(match: TournamentMatch) {
        getMainFragmentNavigator?.setMainFragment(MatchFragment.newInstance(match.url)) {
            addToBackStack(null)
        }
    }
}
