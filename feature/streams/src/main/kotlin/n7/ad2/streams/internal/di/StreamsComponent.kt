package n7.ad2.streams.internal.di

import dagger.Component
import n7.ad2.streams.api.StreamsDependencies
import n7.ad2.streams.internal.StreamsFragment
import n7.ad2.streams.internal.StreamsViewModel
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [
        StreamsDependencies::class
    ],
    modules = [
        RetrofitModule::class
    ],
)
internal interface StreamsComponent {

    @Component.Factory
    interface Factory {
        fun create(dependencies: StreamsDependencies): StreamsComponent
    }

    fun inject(streamsFragment: StreamsFragment)

    val streamsViewModelFactory: StreamsViewModel.Factory

}