package n7.ad2.item_page.internal

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import n7.ad2.android.findDependencies
import n7.ad2.item_page.R
import n7.ad2.item_page.databinding.FragmentItemInfoBinding
import n7.ad2.item_page.internal.adapter.ItemInfoAdapter
import n7.ad2.item_page.internal.adapter.ItemPageDecorator
import n7.ad2.item_page.internal.di.DaggerItemPageComponent
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import n7.ad2.media_player.AudioExoPlayer
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

    private val audioExoPlayer: AudioExoPlayer by lazyUnsafe { AudioExoPlayer(requireContext(), lifecycle) }
    private val itemPageDecorator = ItemPageDecorator()

    //    private val infoPopupWindow: InfoPopupWindow by lazy(LazyThreadSafetyMode.NONE) { n7.ad2.hero_page.internal.info.InfoPopupWindow(requireContext(), lifecycle) }
    private val itemName: String by lazy(LazyThreadSafetyMode.NONE) { requireArguments().getString(ITEM_NAME)!! }

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarsInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updatePadding(top = statusBarsInsets.top, bottom = navigationBarsInsets.bottom)
            insets
        }
    }

    private fun setupToolbar(title: String) {
        binding.toolbar.title = title
    }

    private fun setupItemInfoRecyclerView() {
        val itemInfoAdapter = ItemInfoAdapter(layoutInflater, audioExoPlayer)
        binding.rv.apply {
            adapter = itemInfoAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(itemPageDecorator)
        }
        viewModel.voItemInfo.observe(viewLifecycleOwner, itemInfoAdapter::submitList)
    }


}