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
        val url =
            "http://usher.twitch.tv/api/channel/hls/stray228.m3u8?player=twitchweb&&token=%7B%22adblock%22%3Afalse%2C%22authorization%22%3A%7B%22forbidden%22%3Afalse%2C%22reason%22%3A%22%22%7D%2C%22blackout_enabled%22%3Afalse%2C%22channel%22%3A%22stray228%22%2C%22channel_id%22%3A40488774%2C%22chansub%22%3A%7B%22restricted_bitrates%22%3A%5B%5D%2C%22view_until%22%3A1924905600%7D%2C%22ci_gb%22%3Afalse%2C%22geoblock_reason%22%3A%22%22%2C%22device_id%22%3Anull%2C%22expires%22%3A1634933421%2C%22extended_history_allowed%22%3Afalse%2C%22game%22%3A%22%22%2C%22hide_ads%22%3Afalse%2C%22https_required%22%3Atrue%2C%22mature%22%3Afalse%2C%22partner%22%3Afalse%2C%22platform%22%3A%22web%22%2C%22player_type%22%3A%22embed%22%2C%22private%22%3A%7B%22allowed_to_view%22%3Atrue%7D%2C%22privileged%22%3Afalse%2C%22role%22%3A%22%22%2C%22server_ads%22%3Atrue%2C%22show_ads%22%3Atrue%2C%22subscriber%22%3Afalse%2C%22turbo%22%3Afalse%2C%22user_id%22%3Anull%2C%22user_ip%22%3A%2237.214.79.246%22%2C%22version%22%3A2%7D&sig=f31234faf69da48ea2566b94d3de8134a97df167&allow_audio_only=true&allow_source=true&type=any&p=1"
        playUrl(url)
    }

    private fun playUrl(url: String) {
        viewModel.load(streamerName)
        viewModel.url.observe(viewLifecycleOwner) { realUrl ->
            val dataSource = DefaultHttpDataSource.Factory()
                .setUserAgent(getString(R.string.app_name))
                .setDefaultRequestProperties(mapOf(
                    "Referer" to "https://player.twitch.tv",
                    "Origin" to "https://player.twitch.tv"
                ))
            val mediaSource = HlsMediaSource.Factory(dataSource)
                .createMediaSource(MediaItem.fromUri(realUrl.toUri()))
            player.setMediaSource(mediaSource)
            player.prepare()
            player.play()
        }

    }

}