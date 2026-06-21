package n7.ad2.hero.page.internal.responses

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import n7.ad2.android.findDependencies
import n7.ad2.core.ui.content
import n7.ad2.hero.page.internal.di.DaggerHeroPageComponent
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import n7.ad2.mediaplayer.AudioExoPlayer
import n7.ad2.core.ui.showDialogError
import javax.inject.Inject

class ResponsesFragment : Fragment() {

    companion object {
        private const val HERO_NAME = "HERO_NAME"
        fun newInstance(heroName: String): ResponsesFragment = ResponsesFragment().apply {
            arguments = bundleOf(HERO_NAME to heroName)
        }
    }

    @Inject lateinit var responsesViewModelFactory: ResponsesViewModel.Factory

    @Inject lateinit var audioExoPlayerFactory: AudioExoPlayer.Factory

    private val heroName by lazyUnsafe { requireArguments().getString(HERO_NAME)!! }
    private val viewModel: ResponsesViewModel by viewModel { responsesViewModelFactory.create(heroName) }
    private val audioExoPlayer by lazyUnsafe { audioExoPlayerFactory.create(lifecycle) }
    private val downloadResponseManager by lazyUnsafe {
        DownloadResponseManager(requireActivity().contentResolver, requireActivity().application, lifecycle)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerHeroPageComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        downloadResponseManager.setDownloadListener { result ->
            when (result) {
                is DownloadResult.Success -> viewModel.refreshResponses()
                is DownloadResult.Failed -> showDialogError(result.error)
                is DownloadResult.InProgress -> Unit
            }
        }
        audioExoPlayer.playerStateListener = { state ->
            when (state) {
                AudioExoPlayer.PlayerState.Ended -> Unit
                is AudioExoPlayer.PlayerState.Error -> showDialogError(state.error)
            }
        }
        return content {
            ResponsesScreen(
                viewModel = viewModel,
                onPlay = { audioExoPlayer.play(it.audioUrl) },
                onDownload = { body ->
                    val id = downloadResponseManager.download(body)
                    body.downloadID = id
                },
            )
        }
    }
}
