package n7.ad2.di

import n7.ad2.dagger.Dependencies
import n7.ad2.dagger.DependenciesKey
import n7.ad2.database_guides.api.DatabaseDependencies
import n7.ad2.drawer.api.DrawerDependencies
import n7.ad2.games.api.GamesDependencies
import n7.ad2.hero.page.api.HeroPageDependencies
import n7.ad2.heroes.ui.api.HeroesDependencies
import n7.ad2.itempage.api.ItemPageDependencies
import n7.ad2.items.api.ItemsDependencies
import n7.ad2.news.api.NewsDependencies
import n7.ad2.streams.api.StreamsDependencies
import n7.ad2.tournaments.api.TournamentsDependencies

@dagger.Module
interface ComponentDependenciesModule {

    @dagger.Binds
    @dagger.multibindings.IntoMap
    @DependenciesKey(StreamsDependencies::class)
    fun provideStreamsDependencies(impl: ApplicationComponent): Dependencies

    @dagger.Binds
    @dagger.multibindings.IntoMap
    @DependenciesKey(DatabaseDependencies::class)
    fun provideDatabaseDependencies(impl: ApplicationComponent): Dependencies

    @dagger.Binds
    @dagger.multibindings.IntoMap
    @DependenciesKey(DrawerDependencies::class)
    fun provideDrawerDependencies(impl: ApplicationComponent): Dependencies

    @dagger.Binds
    @dagger.multibindings.IntoMap
    @DependenciesKey(HeroesDependencies::class)
    fun provideHeroesDependencies(impl: ApplicationComponent): Dependencies

    @dagger.Binds
    @dagger.multibindings.IntoMap
    @DependenciesKey(ItemsDependencies::class)
    fun provideItemsDependencies(impl: ApplicationComponent): Dependencies

    @dagger.Binds
    @dagger.multibindings.IntoMap
    @DependenciesKey(GamesDependencies::class)
    fun provideGamesDependencies(impl: ApplicationComponent): Dependencies

    @dagger.Binds
    @dagger.multibindings.IntoMap
    @DependenciesKey(TournamentsDependencies::class)
    fun provideTournamentsDependencies(impl: ApplicationComponent): Dependencies

    @dagger.Binds
    @dagger.multibindings.IntoMap
    @DependenciesKey(HeroPageDependencies::class)
    fun provideHeroPageDependencies(impl: ApplicationComponent): Dependencies

    @dagger.Binds
    @dagger.multibindings.IntoMap
    @DependenciesKey(NewsDependencies::class)
    fun provideNewsDependencies(impl: ApplicationComponent): Dependencies

    @dagger.Binds
    @dagger.multibindings.IntoMap
    @DependenciesKey(ItemPageDependencies::class)
    fun provideItemPageDependencies(impl: ApplicationComponent): Dependencies

}