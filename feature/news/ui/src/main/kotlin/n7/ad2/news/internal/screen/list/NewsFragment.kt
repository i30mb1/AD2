package n7.ad2.news.internal.screen.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import javax.inject.Inject
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.findDependencies
import n7.ad2.android.getMainFragmentNavigator
import n7.ad2.app.logger.Logger
import n7.ad2.ktx.viewModel
import n7.ad2.news.internal.di.DaggerNewsComponent
import n7.ad2.news.internal.screen.article.ArticleFragment
import n7.ad2.news.internal.screen.list.compose.NewsScreen
import n7.ad2.ui.ComposeView

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
        getMainFragmentNavigator.setMainFragment(ArticleFragment.getInstance(newsID)) {
            addToBackStack(null)
        }
    }

}
