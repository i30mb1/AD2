package n7.ad2.di

import n7.ad2.android.Dependencies
import n7.ad2.dagger.DependenciesKey
import n7.ad2.streams.api.StreamsDependencies

@dagger.Module
interface StreamsModule {

    @dagger.Binds
    @dagger.multibindings.IntoMap
    @DependenciesKey(StreamsDependencies::class)
    fun bindStreamsDependencies(impl: MainActivityComponent): Dependencies

}