package n7.ad2.di

import n7.ad2.android.Dependencies
import n7.ad2.dagger.DependenciesKey
import n7.ad2.database_guides.api.DatabaseDependencies
import n7.ad2.drawer.api.DrawerDependencies
import n7.ad2.games.api.GamesDependencies
import n7.ad2.heroes.api.HeroesDependencies
import n7.ad2.items.api.ItemsDependencies
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

    @dagger.Binds
    @dagger.multibindings.IntoMap
    @DependenciesKey(HeroesDependencies::class)
    fun provideHeroesDependencies(impl: MainActivityComponent): Dependencies

    @dagger.Binds
    @dagger.multibindings.IntoMap
    @DependenciesKey(ItemsDependencies::class)
    fun provideItemsDependencies(impl: MainActivityComponent): Dependencies

    @dagger.Binds
    @dagger.multibindings.IntoMap
    @DependenciesKey(GamesDependencies::class)
    fun provideGamesDependencies(impl: MainActivityComponent): Dependencies

}