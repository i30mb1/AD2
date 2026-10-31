package n7.ad2.games.internal.leaderboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.Fragment
import n7.ad2.android.findDependencies
import n7.ad2.core.ui.content
import n7.ad2.games.internal.di.DaggerGamesComponent
import n7.ad2.ktx.viewModel
import javax.inject.Inject

internal class LeaderboardFragment : Fragment() {

    @Inject lateinit var leaderboardViewModelFactory: LeaderboardViewModel.Factory
    private val viewModel: LeaderboardViewModel by viewModel { leaderboardViewModelFactory.create() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerGamesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = content {
        LeaderboardScreen(viewModel.state.collectAsState().value, viewModel::selectMode)
    }
}
