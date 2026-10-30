package n7.ad2.items.internal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import n7.ad2.android.DependenciesMap
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.HasDependencies
import n7.ad2.android.findDependencies
import n7.ad2.android.getMainFragmentNavigator
import n7.ad2.core.ui.content
import n7.ad2.items.internal.di.DaggerItemsComponent
import n7.ad2.items.internal.model.ItemUI
import n7.ad2.ktx.viewModel
import n7.ad2.navigator.Navigator
import javax.inject.Inject

internal class ItemsFragment @JvmOverloads constructor(override var dependenciesMap: DependenciesMap = emptyMap()) :
    Fragment(),
    HasDependencies {

    @Inject lateinit var itemsViewModelFactory: ItemsViewModel.Factory

    @Inject lateinit var navigator: Navigator

    private val viewModel: ItemsViewModel by viewModel { itemsViewModelFactory.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerItemsComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = content {
        val items by viewModel.filteredItems.collectAsStateWithLifecycle()
        val query by viewModel.query.collectAsStateWithLifecycle()
        ItemsScreen(items, query, viewModel::filterItems, ::onItemClick, parentFragment as? DrawerPercentListener)
    }

    private fun onItemClick(model: ItemUI.Body) {
        getMainFragmentNavigator?.setMainFragment(navigator.itemPageApi.getItemPageFragment(model.name)) {
            addToBackStack(null)
        }
        if (!model.viewedByUser) viewModel.updateViewedByUserFieldForItem(model.name)
    }
}
