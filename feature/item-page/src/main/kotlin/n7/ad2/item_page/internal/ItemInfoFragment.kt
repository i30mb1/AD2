package n7.ad2.item_page.internal

import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import n7.ad2.android.findDependencies
import n7.ad2.item_page.R
import n7.ad2.item_page.databinding.FragmentItemInfoBinding
import n7.ad2.item_page.internal.adapter.ItemInfoAdapter
import n7.ad2.item_page.internal.adapter.ItemInfoItemDecorator
import n7.ad2.item_page.internal.di.DaggerItemPageComponent
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import n7.ad2.media_player.AudioExoPlayer
import n7.ad2.ui.InfoPopupWindow
import javax.inject.Inject

class ItemInfoFragment : Fragment(R.layout.fragment_item_info) {

    companion object {
        private const val ITEM_NAME = "ITEM_NAME"
        fun getInstance(itemName: String): ItemInfoFragment = ItemInfoFragment().apply {
            arguments = bundleOf(ITEM_NAME to itemName)
        }
    }

    private var _binding: FragmentItemInfoBinding? = null
    private val binding get() = _binding!!

    private val infoPopupWindow: InfoPopupWindow by lazyUnsafe { InfoPopupWindow(requireContext(), viewLifecycleOwner.lifecycle) }
    private val audioExoPlayer: AudioExoPlayer by lazyUnsafe { AudioExoPlayer(requireActivity().application, viewLifecycleOwner.lifecycle) }
    private val itemPageDecorator = ItemInfoItemDecorator()

    //    private val infoPopupWindow: InfoPopupWindow by lazyUnsafe { InfoPopupWindow(requireContext(), lifecycle) }
    private val itemName: String by lazyUnsafe { requireArguments().getString(ITEM_NAME)!! }

    @Inject lateinit var itemInfoFactory: ItemInfoViewModel.Factory
    private val viewModel: ItemInfoViewModel by viewModel {
        itemInfoFactory.create(itemName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerItemPageComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemInfoBinding.bind(view)

//        viewModel.error.observe(viewLifecycleOwner) { if (it != null) showDialogError(it) }

        setupToolbar(itemName)
        setupItemInfoRecyclerView()
        setupInsets()
        setupAnimation()
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rv.adapter = null
        _binding = null
    }

    private fun showPopup(view: View, text: String) {
        infoPopupWindow.show(view, text)
    }

    private fun setupListeners() {
        audioExoPlayer.playerStateListener = { state ->
            when (state) {
                AudioExoPlayer.PlayerState.Ended -> viewModel.onEndPlayingItem()
                is AudioExoPlayer.PlayerState.Error -> viewModel.onEndPlayingItem()
            }
        }
    }

    private fun setupAnimation() {
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        postponeEnterTransition()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarsInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updatePadding(top = statusBarsInsets.top)
            itemPageDecorator.navigationBarsInsets = navigationBarsInsets.bottom
            binding.rv.invalidateItemDecorations()
            insets
        }
    }

    private fun setupToolbar(title: String) {
        binding.title.text = title
        (binding.appBarLayout.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior(requireContext())
    }

    private fun onPlayIconClick(soundUrl: String) {
        viewModel.onStartPlayingItem(soundUrl)
        audioExoPlayer.play(soundUrl)
    }

    private fun onStopPlaying() {
        viewModel.onEndPlayingItem()
    }

    private fun setupItemInfoRecyclerView() {
        val itemInfoAdapter = ItemInfoAdapter(layoutInflater, ::onPlayIconClick, ::showPopup)
        binding.rv.apply {
            adapter = itemInfoAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(itemPageDecorator)
        }
        viewModel.voItemInfo.observe(viewLifecycleOwner) { list ->
            itemInfoAdapter.submitList(list) { startPostponedEnterTransition() }
        }
    }


}