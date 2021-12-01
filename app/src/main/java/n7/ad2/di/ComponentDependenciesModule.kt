package n7.ad2.di

import n7.ad2.android.Dependencies
import n7.ad2.dagger.DependenciesKey
import n7.ad2.database_guides.api.DatabaseDependencies
import n7.ad2.drawer.api.DrawerDependencies
import n7.ad2.streams.api.StreamsDependencies

@dagger.Module
interface ComponentDependenciesModule {

    @dagger.Binds
    @dagger.multibindings.IntoMap
    @DependenciesKey(StreamsDependencies::class)
    fun provideStreamsDependencies(impl: MainActivityComponent): Dependencies

    @dagger.Binds
    @dagger.multibindings.IntoMap
    @DependenciesKey(DatabaseDependencies::class)
    fun provideDatabaseDependencies(impl: MainActivityComponent): Dependencies

    @dagger.Binds
    @dagger.multibindings.IntoMap
    @DependenciesKey(DrawerDependencies::class)
    fun provideDrawerDependencies(impl: MainActivityComponent): Dependencies

}