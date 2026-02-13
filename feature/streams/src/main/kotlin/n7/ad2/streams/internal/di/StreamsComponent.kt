package n7.ad2.streams.internal.di

import dagger.Component
import n7.ad2.streams.api.StreamsDependencies
import n7.ad2.streams.internal.StreamsFragment
import n7.ad2.streams.internal.StreamsViewModel
import n7.ad2.streams.internal.stream.StreamActivity

@Component(
    dependencies = [
        StreamsDependencies::class,
    ],
    modules = [
        StreamsModule::class,
    ],
)
internal interface StreamsComponent {

    @Component.Factory
    interface Factory {
        fun create(dependencies: StreamsDependencies): StreamsComponent
    }

    fun inject(streamsFragment: StreamsFragment)
    fun inject(streamActivity: StreamActivity)

    val streamsViewModelFactory: StreamsViewModel.Factory
}
