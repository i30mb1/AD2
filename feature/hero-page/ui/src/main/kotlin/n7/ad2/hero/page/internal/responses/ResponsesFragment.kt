package n7.ad2.hero.page.internal.responses

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import n7.ad2.android.findDependencies
import n7.ad2.core.ui.content
import n7.ad2.core.ui.showDialogError
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.di.DaggerHeroPageComponent
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponse
import n7.ad2.hero.page.internal.responses.domain.vo.key
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import n7.ad2.mediaplayer.AudioExoPlayer
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
    private val ringtoneSetter by lazyUnsafe { ResponseRingtoneSetter(requireContext().applicationContext) }

    private var playingKey by mutableStateOf<String?>(null)
    private var downloading by mutableStateOf(emptySet<String>())
    private val downloadIds = mutableMapOf<Long, String>()
    private var settingTarget by mutableStateOf<ResponseRingtoneSetter.Target?>(null)

    // waits while the user is on the "modify system settings" screen
    private var pendingRingtone: Pair<VOResponse.Body, ResponseRingtoneSetter.Target>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerHeroPageComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        downloadResponseManager.setDownloadListener { result ->
            when (result) {
                is DownloadResult.Success -> {
                    downloadIds.remove(result.downloadID)?.let { key -> downloading = downloading - key }
                    viewModel.refreshResponses()
                }
                is DownloadResult.Failed -> {
                    downloadIds.clear()
                    downloading = emptySet()
                    showDialogError(result.error)
                }
                is DownloadResult.InProgress -> Unit
            }
        }
        audioExoPlayer.playerStateListener = { state ->
            playingKey = null
            if (state is AudioExoPlayer.PlayerState.Error) showDialogError(state.error)
        }
        return content {
            ResponsesScreen(
                viewModel = viewModel,
                playingKey = playingKey,
                progress = audioExoPlayer::progress,
                downloading = downloading,
                settingTarget = settingTarget,
                onPlay = ::togglePlay,
                onDownload = ::download,
                onSetAs = ::setAsRingtone,
            )
        }
    }

    override fun onResume() {
        super.onResume()
        val pending = pendingRingtone ?: return
        pendingRingtone = null
        if (ringtoneSetter.canWriteSettings()) setAsRingtone(pending.first, pending.second)
    }

    override fun onStop() {
        super.onStop()
        // the player releases itself in onStop, so nothing is playing anymore
        playingKey = null
    }

    private fun togglePlay(item: VOResponse.Body) {
        if (playingKey == item.key) {
            audioExoPlayer.stop()
            playingKey = null
        } else {
            audioExoPlayer.play(item.audioUrl)
            playingKey = item.key
        }
    }

    private fun download(item: VOResponse.Body) {
        val id = downloadResponseManager.download(item) ?: return
        item.downloadID = id
        downloadIds[id] = item.titleForFile
        downloading = downloading + item.titleForFile
    }

    private fun setAsRingtone(item: VOResponse.Body, target: ResponseRingtoneSetter.Target) {
        if (settingTarget != null) return
        if (!ringtoneSetter.canWriteSettings()) {
            pendingRingtone = item to target
            Toast.makeText(requireContext(), R.string.response_write_settings_request, Toast.LENGTH_LONG).show()
            startActivity(ringtoneSetter.writeSettingsIntent())
            return
        }
        settingTarget = target
        viewLifecycleOwner.lifecycleScope.launch {
            val result = runCatching { ringtoneSetter.set(item, target) }
            settingTarget = null
            val message = when {
                result.isFailure -> R.string.response_set_failed
                target == ResponseRingtoneSetter.Target.CALL -> R.string.response_set_call_done
                target == ResponseRingtoneSetter.Target.NOTIFICATION -> R.string.response_set_notification_done
                else -> R.string.response_set_alarm_done
            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }
}
