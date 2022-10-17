@file:OptIn(ExperimentalMaterialApi::class)

package ad2.n7.news.internal

import ad2.n7.news.internal.di.DaggerNewsComponent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.fragment.app.Fragment
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.findDependencies
import n7.ad2.android.getNavigator
import n7.ad2.ktx.viewModel
import n7.ad2.logger.Logger
import n7.ad2.ui.ComposeView
import javax.inject.Inject

internal class NewsFragment : Fragment() {

    companion object {
        fun getInstance() = NewsFragment()
    }

    @Inject lateinit var newsViewModelFactory: NewsViewModel.Factory
    @Inject lateinit var logger: Logger

    private val viewModel: NewsViewModel by viewModel { newsViewModelFactory.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerNewsComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView { NewsScreen(viewModel, parentFragment as DrawerPercentListener, ::onNewsClicked) }
    }

    private fun onNewsClicked(newsID: Int) {
        getNavigator.setMainFragment(SingleNewsFragment.getInstance()) {
            addToBackStack(null)
        }
    }

}