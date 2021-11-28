package n7.ad2.ui.items

import ad2.n7.android.DrawerPercentListener
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.ad2.R
import n7.ad2.databinding.FragmentItemsBinding
import n7.ad2.di.injector
import n7.ad2.ui.itemInfo.ItemInfoActivity
import n7.ad2.ui.items.adapter.ItemsItemDecorator
import n7.ad2.ui.items.adapter.ItemsListAdapter
import n7.ad2.ui.items.domain.vo.VOItemBody
import n7.ad2.utils.viewModel

class ItemsFragment : Fragment(R.layout.fragment_items) {

    companion object {
        fun getInstance() = ItemsFragment()
    }

    private var _binding: FragmentItemsBinding? = null
    private val binding: FragmentItemsBinding get() = _binding!!
    private val viewModel: ItemsViewModel by viewModel { injector.itemsViewModel }
    private val itemsItemDecorator = ItemsItemDecorator()
    private val onItemClick: (hero: VOItemBody) -> Unit = { model ->
        startItemInfoFragment(model)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemsBinding.bind(view)
        setupAdapter()
    }

    private fun startItemInfoFragment(model: VOItemBody) {
        val intent = Intent(requireContext(), ItemInfoActivity::class.java)
        intent.putExtra(ItemInfoActivity.ITEM_NAME, model.name)
        startActivity(intent)

        if (!model.viewedByUser) viewModel.updateViewedByUserFieldForItem(model.name)
    }

    private fun setupAdapter() {
        val spanSizeItem = 1
        val spanSizeItemHeader = 4

        val itemsAdapter = ItemsListAdapter(layoutInflater, onItemClick)
        val gridLayoutManager = GridLayoutManager(context, spanSizeItemHeader)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = when (itemsAdapter.getItemViewType(position)) {
                R.layout.item_header -> spanSizeItemHeader
                else -> spanSizeItem
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarsInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            itemsItemDecorator.statusBarsInsets = statusBarsInsets.top
            itemsItemDecorator.navigationBarsInsets = navigationBarsInsets.bottom
            insets
        }
        (parentFragment as DrawerPercentListener).setDrawerPercentListener { percent ->
            itemsItemDecorator.percent = percent
            binding.rv.invalidateItemDecorations()
        }

        binding.rv.apply {
            setHasFixedSize(true)
            addItemDecoration(itemsItemDecorator)
            layoutManager = gridLayoutManager
            adapter = itemsAdapter
        }

        viewModel.filteredItems.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach(itemsAdapter::submitList)
            .launchIn(lifecycleScope)
    }

}