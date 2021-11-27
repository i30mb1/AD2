package n7.ad2.di

import ad2.n7.android.Dependencies
import ad2.n7.dagger.DependenciesKey
import n7.ad2.streams.api.StreamsDependencies

@dagger.Module
interface StreamsModule {

    @dagger.Binds
    @dagger.multibindings.IntoMap
    @DependenciesKey(StreamsDependencies::class)
    fun bindStreamsDependencies(impl: MainActivityComponent): Dependencies

}