package n7.ad2.news.ui.internal.di

import n7.ad2.news.ui.api.NewsDependencies
import n7.ad2.news.ui.internal.NewsWorker
import n7.ad2.news.ui.internal.screen.article.ArticleFragment
import n7.ad2.news.ui.internal.screen.news.NewsFragment
import n7.ad2.news.ui.internal.screen.news.NewsViewModel

@dagger.Component(
    dependencies = [
        NewsDependencies::class
    ],
)
internal interface NewsComponent {

    @dagger.Component.Factory
    interface Factory {
        fun create(dependencies: NewsDependencies): NewsComponent
    }

    fun inject(newsFragment: NewsFragment)
    fun inject(singleNewsFragment: ArticleFragment)
    fun inject(newsWorker: NewsWorker)

    val newsViewModelFactory: NewsViewModel.Factory

}
