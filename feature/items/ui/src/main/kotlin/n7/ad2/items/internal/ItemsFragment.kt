package n7.ad2.items.internal

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.ad2.android.DependenciesMap
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.HasDependencies
import n7.ad2.android.findDependencies
import n7.ad2.android.getMainFragmentNavigator
import n7.ad2.feature.items.ui.R
import n7.ad2.feature.items.ui.databinding.FragmentItemsBinding
import n7.ad2.items.internal.adapter.ItemsItemDecorator
import n7.ad2.items.internal.adapter.ItemsListAdapter
import n7.ad2.items.internal.di.DaggerItemsComponent
import n7.ad2.items.internal.model.ItemUI
import n7.ad2.ktx.viewModel
import n7.ad2.navigator.Navigator

internal class ItemsFragment(
    override var dependenciesMap: DependenciesMap,
) : Fragment(R.layout.fragment_items), HasDependencies {

    @Inject lateinit var itemsViewModelFactory: ItemsViewModel.Factory
    @Inject lateinit var navigator: Navigator

    private var _binding: FragmentItemsBinding? = null
    private val binding: FragmentItemsBinding get() = _binding!!
    private val viewModel: ItemsViewModel by viewModel { itemsViewModelFactory.create() }
    private val itemsItemDecorator = ItemsItemDecorator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerItemsComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemsBinding.bind(view)
        setupAdapter()
        setupAnimation()
        postponeEnterTransition()
    }

    private fun setupAnimation() {

    }

    private fun onItemClick(model: ItemUI.Body, view: ImageView) {
        getMainFragmentNavigator?.setMainFragment(navigator.itemPageApi.getItemPageFragment(model.name)) {
            setReorderingAllowed(true)
            addSharedElement(view, view.transitionName)
            addToBackStack(null)
        }
        if (!model.viewedByUser) viewModel.updateViewedByUserFieldForItem(model.name)
    }

    private fun setupAdapter() {
        val spanSizeItem = 1
        val spanSizeItemHeader = 4

        val itemsAdapter = ItemsListAdapter(layoutInflater, ::onItemClick)
        val gridLayoutManager = GridLayoutManager(context, spanSizeItemHeader)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = when (itemsAdapter.getItemViewType(position)) {
                n7.ad2.core.ui.R.layout.item_header -> spanSizeItemHeader
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
        (parentFragment as? DrawerPercentListener)?.setDrawerPercentListener { percent ->
            itemsItemDecorator.percent = percent
            binding.rv.invalidateItemDecorations()
        }

        binding.rv.apply {
            setHasFixedSize(true)
            addItemDecoration(itemsItemDecorator)
            layoutManager = gridLayoutManager
            adapter = itemsAdapter
        }

        viewModel.filteredItems.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { list -> itemsAdapter.submitList(list) { startPostponedEnterTransition() } }
            .launchIn(lifecycleScope)
    }

}
