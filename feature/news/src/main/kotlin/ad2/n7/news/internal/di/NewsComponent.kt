package ad2.n7.news.internal.di

import ad2.n7.news.api.NewsDependencies
import ad2.n7.news.internal.NewsViewModel
import ad2.n7.news.internal.NewsWorker
import ad2.n7.news.internal.SingleNewsFragment
import ad2.n7.news.internal.list.NewsFragment

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
    fun inject(singleNewsFragment: SingleNewsFragment)
    fun inject(newsWorker: NewsWorker)

    val newsViewModelFactory: NewsViewModel.Factory

}