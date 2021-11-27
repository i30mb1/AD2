package n7.ad2.streams.internal.di

import dagger.Component
import n7.ad2.streams.api.RetrofitModule
import n7.ad2.streams.api.StreamsDependencies
import n7.ad2.streams.api.StreamsFragment
import n7.ad2.streams.internal.StreamsViewModel

@Component(
    dependencies = [
        StreamsDependencies::class
    ],
    modules = [
        RetrofitModule::class
    ],
)
interface StreamsComponent {

    @Component.Factory
    interface Factory {
        fun create(dependencies: StreamsDependencies): StreamsComponent
    }

    fun inject(streamsFragment: StreamsFragment)

    val streamsViewModelFactory: StreamsViewModel.Factory

}