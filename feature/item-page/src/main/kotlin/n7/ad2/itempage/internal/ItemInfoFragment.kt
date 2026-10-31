package n7.ad2.itempage.internal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import n7.ad2.android.findDependencies
import n7.ad2.android.getMainFragmentNavigator
import n7.ad2.core.ui.content
import n7.ad2.itempage.internal.compose.ItemPageScreen
import n7.ad2.itempage.internal.di.DaggerItemPageComponent
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import n7.ad2.mediaplayer.AudioExoPlayer
import n7.ad2.navigator.Navigator
import javax.inject.Inject

@UnstableApi
internal class ItemInfoFragment : Fragment() {

    companion object {
        private const val ITEM_NAME = "ITEM_NAME"
        fun getInstance(itemName: String): ItemInfoFragment = ItemInfoFragment().apply {
            arguments = bundleOf(ITEM_NAME to itemName)
        }
    }

    private val audioExoPlayer: AudioExoPlayer by lazyUnsafe { AudioExoPlayer(requireActivity().application, viewLifecycleOwner.lifecycle) }
    private val itemName: String by lazyUnsafe { requireArguments().getString(ITEM_NAME)!! }

    @Inject lateinit var itemInfoFactory: ItemInfoViewModel.Factory

    @Inject lateinit var navigator: Navigator

    private val viewModel: ItemInfoViewModel by viewModel {
        itemInfoFactory.create(itemName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerItemPageComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = content {
        val state by viewModel.state.collectAsStateWithLifecycle()
        val playingUrl by viewModel.playingUrl.collectAsStateWithLifecycle()
        ItemPageScreen(
            state = state,
            playingUrl = playingUrl,
            onBackClicked = { requireActivity().onBackPressedDispatcher.onBackPressed() },
            onRetryClicked = viewModel::load,
            onItemClicked = ::openItem,
            onHeroClicked = ::openHero,
            onPlayClicked = ::onPlayClicked,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audioExoPlayer.playerStateListener = { state ->
            when (state) {
                AudioExoPlayer.PlayerState.Ended -> viewModel.onEndPlaying()
                is AudioExoPlayer.PlayerState.Error -> viewModel.onEndPlaying()
            }
        }
    }

    private fun onPlayClicked(url: String) {
        if (viewModel.playingUrl.value == url) {
            audioExoPlayer.stop()
            viewModel.onEndPlaying()
        } else {
            viewModel.onStartPlaying(url)
            audioExoPlayer.play(url)
        }
    }

    private fun openItem(key: String) {
        getMainFragmentNavigator?.setMainFragment(getInstance(key)) {
            addToBackStack(null)
        }
    }

    private fun openHero(name: String) {
        getMainFragmentNavigator?.setMainFragment(navigator.heroPageApi.getPagerFragment(name)) {
            addToBackStack(null)
        }
    }
}
