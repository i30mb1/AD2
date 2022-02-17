package n7.ad2.di

import ad2.n7.news.api.NewsDependencies
import android.app.Application
import dagger.BindsInstance
import dagger.Component
import n7.ad2.UpdateManagerModule
import n7.ad2.app_preference.AppPreferenceModule
import n7.ad2.coroutines.CoroutineModule
import n7.ad2.dagger.ApplicationScope
import n7.ad2.database_guides.api.DatabaseDependencies
import n7.ad2.database_guides.api.DatabaseModule
import n7.ad2.drawer.api.DrawerDependencies
import n7.ad2.games.api.GamesDependencies
import n7.ad2.hero_page.api.HeroPageDependencies
import n7.ad2.heroes.api.HeroesDependencies
import n7.ad2.item_page.api.ItemPageDependencies
import n7.ad2.items.api.ItemsDependencies
import n7.ad2.streams.api.StreamsDependencies
import n7.ad2.tournaments.api.TournamentsDependencies
import n7.ad2.ui.MainActivity2
import n7.ad2.ui.MyApplication

@ApplicationScope
@dagger.Component(
    modules = [
        ComponentDependenciesModule::class,
        CoroutineModule::class,
        UpdateManagerModule::class,
        MoshiModule::class,
        RetrofitModule::class,
        ApplicationModule::class,
        AppPreferenceModule::class,
        DatabaseModule::class,
    ]
)
interface ApplicationComponent : StreamsDependencies,
    DrawerDependencies,
    HeroesDependencies,
    ItemPageDependencies,
    GamesDependencies,
    TournamentsDependencies,
    NewsDependencies,
    HeroPageDependencies,
    ItemsDependencies,
    DatabaseDependencies {

    fun inject(application: MyApplication)
    fun inject(mainActivity: MainActivity2)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Application): ApplicationComponent
    }
}