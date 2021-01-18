package n7.ad2.ui.itemInfo

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import n7.ad2.R
import n7.ad2.data.source.local.Locale
import n7.ad2.databinding.FragmentItemInfoBinding
import n7.ad2.di.injector
import n7.ad2.ui.heroInfo.InfoPopupWindow
import n7.ad2.ui.heroPage.AudioExoPlayer
import n7.ad2.ui.heroPage.showDialogError
import n7.ad2.utils.StickyHeaderDecorator

class ItemInfoFragment : Fragment(R.layout.fragment_item_info) {

    private var _binding: FragmentItemInfoBinding? = null
    private val binding get() = _binding!!
    lateinit var audioExoPlayer: AudioExoPlayer
    private val infoPopupWindow: InfoPopupWindow by lazy { InfoPopupWindow(requireContext(), lifecycle) }
    private val itemName: String by lazy { requireArguments().getString(ITEM_NAME)!! }
    private val viewModel: ItemInfoViewModel by viewModels { ItemInfoViewModel.provideFactory(injector.itemInfoViewModel, itemName) }

    companion object {
        private const val ITEM_NAME = "ITEM_NAME"
        fun newInstance(itemName: String): ItemInfoFragment = ItemInfoFragment().apply {
            arguments = bundleOf(ITEM_NAME to itemName)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemInfoBinding.bind(view)

        viewModel.error.observe(viewLifecycleOwner) { if (it != null) showDialogError(it) }

        loadItemInfo(itemName)
        setupToolbar(itemName)
        setupAudioPlayer()
        setupItemInfoRecyclerView()
    }

    private fun loadItemInfo(itemName: String) {
        val locale = Locale.valueOf(getString(R.string.locale))
        viewModel.loadItemInfo(itemName, locale)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbar(title: String) {
        binding.toolbar.title = title
    }

    private fun setupAudioPlayer() {
        audioExoPlayer = AudioExoPlayer(requireActivity().application, lifecycle)
        audioExoPlayer.setErrorListener(::showDialogError)
    }

    private fun setupItemInfoRecyclerView() {
        val itemInfoAdapter = ItemInfoAdapter(audioExoPlayer, infoPopupWindow)
        binding.rv.apply {
            adapter = itemInfoAdapter
            addItemDecoration(StickyHeaderDecorator(itemInfoAdapter, this))
        }
        viewModel.voItemInfo.observe(viewLifecycleOwner, itemInfoAdapter::submitList)
    }


}