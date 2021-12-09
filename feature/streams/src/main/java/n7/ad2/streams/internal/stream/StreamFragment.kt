package n7.ad2.streams.internal.stream

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.launch
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.streams.R
import n7.ad2.streams.databinding.FragmentStreamBinding

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
//    private val viewModel: StreamViewModel by viewModel { injector.streamViewModel }

    private lateinit var binding: FragmentStreamBinding
//    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // todo but how to animate recyclerView???
        enterTransition = MaterialFadeThrough().apply {
            duration = 3000
        }
        exitTransition = MaterialFadeThrough().apply {
            duration = 3000
        }
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
//        player = SimpleExoPlayer.Builder(requireContext()).build()
//        binding.playerView.player = player
//        playUrl()
    }

    private fun playUrl() {
//        viewModel.load(streamerName)
//        viewModel.url.observe(viewLifecycleOwner) { realUrl ->
//            if (realUrl == null) return@observe
//            val dataSource = DefaultHttpDataSource.Factory()
//
//            val mediaSource = HlsMediaSource.Factory(dataSource).createMediaSource(MediaItem.fromUri(realUrl.toUri()))
//            player.setMediaSource(mediaSource)
//            player.prepare()
//            player.play()
//        }

    }

    override fun onDestroy() {
        super.onDestroy()
//        player.stop()
    }

}