package n7.ad2.ui.itemInfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import n7.ad2.R
import n7.ad2.data.source.local.Locale
import n7.ad2.databinding.FragmentItemsBinding
import n7.ad2.di.injector
import n7.ad2.ui.heroInfo.DescriptionsListAdapter
import n7.ad2.ui.heroInfo.InfoPopupWindow
import n7.ad2.ui.heroPage.AudioExoPlayer
import n7.ad2.ui.heroPage.showDialogError
import n7.ad2.utils.viewModel

class ItemInfoFragment : Fragment(R.layout.fragment_item_info) {

    private lateinit var binding: FragmentItemsBinding
    lateinit var audioExoPlayer: AudioExoPlayer
    private lateinit var infoPopupWindow: InfoPopupWindow
    private val viewModel: ItemInfoViewModel by viewModel { injector.itemInfoViewModel }

    companion object {
        private const val ITEM_NAME = "ITEM_NAME"
        fun newInstance(): ItemInfoFragment = ItemInfoFragment()
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentItemsBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
        }

        viewModel.loadItemInfo(requireArguments().getString(ITEM_NAME)!!, Locale.valueOf(getString(R.string.locale)))
        setupAudioPlayer()
        setupItemInfoRecyclerView()
    }

    private fun setupAudioPlayer() {
        audioExoPlayer = AudioExoPlayer(requireActivity().application, lifecycle)
        audioExoPlayer.setErrorListener(::showDialogError)
    }

    private fun setupItemInfoRecyclerView() {
        val adapter = DescriptionsListAdapter(audioExoPlayer, infoPopupWindow)
        binding.rv.adapter = adapter
        viewModel.voItemInfo.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }


}