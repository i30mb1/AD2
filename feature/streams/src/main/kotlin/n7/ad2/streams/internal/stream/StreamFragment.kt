package n7.ad2.streams.internal.stream

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.session.MediaSession
import kotlinx.coroutines.launch
import n7.ad2.android.findDependencies
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import n7.ad2.streams.R
import n7.ad2.streams.databinding.FragmentStreamBinding
import n7.ad2.streams.internal.di.DaggerStreamsComponent
import javax.inject.Inject

@UnstableApi
class StreamFragment : Fragment(R.layout.fragment_stream) {

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

    private lateinit var binding: FragmentStreamBinding
    private lateinit var player: ExoPlayer

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerStreamsComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStreamBinding.bind(view)
        initializePlayer()
        lifecycleScope.launch {
//            viewModel.error
//                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
//                .collect(::showDialogError)
        }
        setupOnBackPressed()
    }

    private fun setupOnBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(requireContext()).build()
        val session = MediaSession.Builder(requireContext(), player).build()
        binding.playerView.player = player
        playUrl()
    }

    private fun playUrl() {
        viewModel.load(streamerName)
        viewModel.url.observe(viewLifecycleOwner) { realUrl ->
            if (realUrl == null) return@observe
            val dataSource = DefaultHttpDataSource.Factory()

            val mediaSource = HlsMediaSource.Factory(dataSource).createMediaSource(MediaItem.fromUri(realUrl.toUri()))
            player.setMediaSource(mediaSource)
            player.prepare()
            player.play()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
//        player.stop()
    }

}