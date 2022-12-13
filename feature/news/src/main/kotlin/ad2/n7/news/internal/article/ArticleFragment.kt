package ad2.n7.news.internal.article

import ad2.n7.news.internal.article.compose.ArticleScreen
import ad2.n7.news.internal.di.DaggerNewsComponent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import n7.ad2.android.findDependencies
import n7.ad2.ktx.viewModel
import n7.ad2.ui.ComposeView
import javax.inject.Inject

internal class ArticleFragment : Fragment() {

    companion object {
        fun getInstance() = ArticleFragment()
    }

    @Inject lateinit var articleViewModelFactory: ArticleViewModel.Factory
    private val viewModel: ArticleViewModel by viewModel { articleViewModelFactory.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerNewsComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView { ArticleScreen(viewModel) }
    }

}