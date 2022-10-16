package n7.ad2.streams.internal.stream

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.runtime.collectAsState
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.media3.common.util.UnstableApi
import n7.ad2.android.findDependencies
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import n7.ad2.streams.internal.di.DaggerStreamsComponent
import n7.ad2.ui.ComposeView
import n7.ad2.ui.compose.AppTheme
import javax.inject.Inject

@UnstableApi
class StreamFragment : Fragment() {

    companion object {
        private const val STREAMER_NAME = "STREAMER_NAME"
        fun newInstance(streamerName: String) = StreamFragment().apply {
            arguments = bundleOf(
                STREAMER_NAME to streamerName
            )
        }
    }

    @Inject lateinit var streamFactory: StreamViewModel.Factory

    private val streamerName by lazyUnsafe { requireArguments().getString(STREAMER_NAME)!! }
    private val viewModel: StreamViewModel by viewModel { streamFactory.create() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerStreamsComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView {
            AppTheme {
                val url = viewModel.url.collectAsState()
                StreamScreen(url.value)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.load(streamerName)
        setupOnBackPressed()
    }

    private fun setupOnBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }

}