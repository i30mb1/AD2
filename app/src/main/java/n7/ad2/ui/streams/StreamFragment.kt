package n7.ad2.ui.streams

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import n7.ad2.R
import n7.ad2.databinding.FragmentStreamBinding
import n7.ad2.di.injector
import n7.ad2.ui.heroPage.showDialogError
import n7.ad2.utils.lazyUnsafe
import n7.ad2.utils.viewModel

class StreamFragment : Fragment(R.layout.fragment_stream) {

    companion object {
        private const val STREAMER_NAME = "STREAMER_NAME"
        fun newInstance(streamerName: String) = StreamFragment().apply {
            arguments = bundleOf(
                STREAMER_NAME to streamerName
            )
        }
    }

    private val streamerName by lazyUnsafe { requireArguments().getString(STREAMER_NAME)!! }
    private val viewModel: StreamViewModel by viewModel { injector.streamViewModel }

    private lateinit var binding: FragmentStreamBinding
    private lateinit var player: ExoPlayer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStreamBinding.bind(view)
        initializePlayer()
        lifecycleScope.launch {
            viewModel.error
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect(::showDialogError)
        }
    }

    private fun initializePlayer() {
        player = SimpleExoPlayer.Builder(requireContext()).build()
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
        player.stop()
    }

}