package ad2.n7.news.internal.di

import ad2.n7.news.api.NewsDependencies
import ad2.n7.news.internal.NewsFragment
import ad2.n7.news.internal.NewsViewModel
import ad2.n7.news.internal.NewsWorker

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
    fun inject(newsWorker: NewsWorker)

    val newsViewModelFactory: NewsViewModel.Factory

}