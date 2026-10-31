package n7.ad2.streams.internal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.findDependencies
import n7.ad2.app.logger.Logger
import n7.ad2.ktx.viewModel
import n7.ad2.streams.internal.di.DaggerStreamsComponent
import n7.ad2.streams.internal.domain.vo.VOStream
import n7.ad2.streams.internal.stream.StreamActivity
import n7.ad2.core.ui.content
import javax.inject.Inject

@UnstableApi
internal class StreamsFragment : Fragment() {

    companion object {
        fun getInstance() = StreamsFragment()
    }

    @Inject lateinit var streamsFactory: StreamsViewModel.Factory

    @Inject lateinit var logger: Logger

    private val viewModel: StreamsViewModel by viewModel { streamsFactory.create() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerStreamsComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = content {
        val streams: LazyPagingItems<VOStream> = viewModel.streams.collectAsLazyPagingItems()
        val language by viewModel.language.collectAsStateWithLifecycle()
        StreamsScreen(streams, language, parentFragment as DrawerPercentListener, viewModel::setLanguage, ::onStreamClicked)
    }

    private fun onStreamClicked(stream: VOStream.Simple) {
        startActivity(StreamActivity.newInstance(requireContext(), stream.streamerName))
    }
}
