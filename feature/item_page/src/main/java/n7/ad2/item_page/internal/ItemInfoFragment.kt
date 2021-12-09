package n7.ad2.item_page.internal

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import n7.ad2.item_page.R
import n7.ad2.item_page.databinding.FragmentItemInfoBinding
import n7.ad2.ktx.viewModel
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

    //    private val audioExoPlayer: n7.ad2.hero_page.AudioExoPlayer by lazy(LazyThreadSafetyMode.NONE) { n7.ad2.hero_page.AudioExoPlayer(requireContext(), lifecycle, ::showDialogError) }
//    private val infoPopupWindow: InfoPopupWindow by lazy(LazyThreadSafetyMode.NONE) { n7.ad2.hero_page.internal.info.InfoPopupWindow(requireContext(), lifecycle) }
    private val itemName: String by lazy(LazyThreadSafetyMode.NONE) { requireArguments().getString(ITEM_NAME)!! }

    @Inject lateinit var itemInfoFactory: ItemInfoViewModel.Factory
    private val viewModel: ItemInfoViewModel by viewModel {
        itemInfoFactory.create(itemName)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemInfoBinding.bind(view)

//        viewModel.error.observe(viewLifecycleOwner) { if (it != null) showDialogError(it) }

        setupToolbar(itemName)
        setupItemInfoRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbar(title: String) {
        binding.toolbar.title = title
    }

    private fun setupItemInfoRecyclerView() {
//        val itemInfoAdapter = ItemInfoAdapter(audioExoPlayer, infoPopupWindow)
        binding.rv.apply {
//            adapter = itemInfoAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
//        viewModel.voItemInfo.observe(viewLifecycleOwner, itemInfoAdapter::submitList)
    }


}