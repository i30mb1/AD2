package n7.ad2.ui.items

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.ad2.R
import n7.ad2.databinding.FragmentItemsBinding
import n7.ad2.databinding.ItemItemBodyBinding
import n7.ad2.di.injector
import n7.ad2.ui.MainActivity
import n7.ad2.ui.itemInfo.ItemInfoActivity
import n7.ad2.ui.items.domain.vo.VOItemBody
import n7.ad2.utils.viewModel

class ItemsFragment : Fragment(R.layout.fragment_items) {

    private val viewModel: ItemsViewModel by viewModel { injector.itemsViewModel }
    private lateinit var binding: FragmentItemsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentItemsBinding.bind(view)

        requireActivity().setTitle(R.string.items)
        setHasOptionsMenu(true)
        setupAdapter()
    }

    fun startItemInfoFragment(model: VOItemBody, binding: ItemItemBodyBinding) {
        val intent = Intent(requireContext(), ItemInfoActivity::class.java)
        intent.putExtra(ItemInfoActivity.ITEM_NAME, model.name)
        startActivity(intent)

        if (!model.viewedByUser) viewModel.updateViewedByUserFieldForItem(model.name)
    }

    private fun setupAdapter() {
        val spanSizeItem = 1
        val spanSizeItemHeader = 4

        val itemsAdapter = ItemsListAdapter(this)
        val gridLayoutManager = GridLayoutManager(context, spanSizeItemHeader).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int) = when (itemsAdapter.getItemViewType(position)) {
                    R.layout.item_item_header -> spanSizeItemHeader
                    else -> spanSizeItem
                }
            }
        }
        binding.rv.apply {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
            adapter = itemsAdapter
        }

        viewModel.filteredItems.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach(itemsAdapter::submitList)
            .launchIn(lifecycleScope)
    }

}