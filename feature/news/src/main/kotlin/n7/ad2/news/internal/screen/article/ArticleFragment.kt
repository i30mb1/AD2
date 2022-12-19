package n7.ad2.news.internal.screen.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import n7.ad2.android.findDependencies
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import n7.ad2.news.internal.di.DaggerNewsComponent
import n7.ad2.news.internal.screen.article.compose.ArticleScreen
import n7.ad2.ui.ComposeView
import javax.inject.Inject

internal class ArticleFragment : Fragment() {

    companion object {
        private const val NEWS_ID = "NEWS_ID"
        fun getInstance(newsID: Int) = ArticleFragment().apply {
            arguments = bundleOf(
                NEWS_ID to newsID,
            )
        }
    }

    @Inject lateinit var articleViewModelFactory: ArticleViewModel.Factory
    private val newsID by lazyUnsafe { requireArguments().getInt(NEWS_ID) }
    private val viewModel: ArticleViewModel by viewModel { articleViewModelFactory.create(newsID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerNewsComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView { ArticleScreen(viewModel) }
    }

}