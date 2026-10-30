package n7.ad2.feature.heroes.ui.internal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.Lazy
import n7.ad2.android.DependenciesMap
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.HasDependencies
import n7.ad2.android.findDependencies
import n7.ad2.android.getMainFragmentNavigator
import n7.ad2.core.ui.content
import n7.ad2.feature.heroes.ui.internal.di.DaggerHeroesComponent
import n7.ad2.feature.heroes.ui.internal.domain.vo.VOHero
import n7.ad2.ktx.viewModel
import n7.ad2.navigator.Navigator
import javax.inject.Inject
import javax.inject.Provider

/**
 * @JvmOverloads - нужен для запуска этого фрагмента когда фарбирка для HeroesFragment не будет предоставлена
 * обычно она будет предоставлена в тестах, в проде зависимости берутся из Application
 */
internal class HeroesFragment @JvmOverloads constructor(override var dependenciesMap: DependenciesMap = emptyMap()) :
    Fragment(),
    HasDependencies {

    @Inject lateinit var navigator: Lazy<Navigator>

    @Inject lateinit var heroesViewModelFactory: Provider<HeroesViewModel.Factory>

    private val viewModel: HeroesViewModel by viewModel { heroesViewModelFactory.get().create() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerHeroesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = content {
        val heroes by viewModel.heroes.collectAsStateWithLifecycle()
        val query by viewModel.query.collectAsStateWithLifecycle()
        HeroesScreen(heroes, query, viewModel::filterHeroes, ::onHeroClicked, parentFragment as? DrawerPercentListener)
    }

    private fun onHeroClicked(model: VOHero.Body) {
        getMainFragmentNavigator?.setMainFragment(navigator.get().heroPageApi.getPagerFragment(model.name)) {
            addToBackStack(null)
        }
        if (!model.viewedByUser) viewModel.updateViewedByUserFieldForHero(model.name)
    }
}
