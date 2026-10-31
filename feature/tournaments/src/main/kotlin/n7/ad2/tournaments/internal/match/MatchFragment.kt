package n7.ad2.tournaments.internal.match

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import n7.ad2.android.findDependencies
import n7.ad2.core.ui.content
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import n7.ad2.navigator.Navigator
import n7.ad2.tournaments.internal.di.DaggerTournamentsComponent
import javax.inject.Inject

internal class MatchFragment : Fragment() {

    companion object {
        private const val URL = "URL"
        fun newInstance(url: String) = MatchFragment().apply { arguments = bundleOf(URL to url) }
    }

    @Inject lateinit var matchViewModelFactory: MatchViewModel.Factory

    @Inject lateinit var navigator: Navigator

    private val url by lazyUnsafe { requireArguments().getString(URL)!! }
    private val viewModel: MatchViewModel by viewModel { matchViewModelFactory.create(url) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerTournamentsComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = content {
        val state = viewModel.state.collectAsState().value
        MatchScreen(state, onBackClicked = { requireActivity().onBackPressedDispatcher.onBackPressed() }, viewModel::load, ::onWatchClicked)
    }

    private fun onWatchClicked(details: MatchDetails) {
        val channel = details.twitchChannel
        val intent = when {
            channel != null -> navigator.streamApi.getStreamIntent(requireContext(), channel)
            else -> Intent(Intent.ACTION_VIEW, Uri.parse(details.streamUrl))
        }
        startActivity(intent)
    }
}
