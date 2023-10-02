package n7.ad2.news.internal.di

import n7.ad2.news.api.NewsDependencies
import n7.ad2.news.internal.NewsWorker
import n7.ad2.news.internal.screen.article.ArticleFragment
import n7.ad2.news.internal.screen.list.NewsFragment
import n7.ad2.news.internal.screen.list.NewsViewModel

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