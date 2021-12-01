package n7.ad2.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import n7.ad2.coroutines.CoroutineModule
import n7.ad2.database_guides.api.DatabaseDependencies
import n7.ad2.database_guides.api.DatabaseModule
import n7.ad2.drawer.api.DrawerDependencies
import n7.ad2.news.NewsViewModel
import n7.ad2.streams.api.StreamsDependencies
import n7.ad2.tournaments.TournamentsViewModel
import n7.ad2.ui.MainActivity
import n7.ad2.ui.MainActivity2
import n7.ad2.ui.MyApplication
import n7.ad2.ui.heroGuide.HeroGuideViewModel
import n7.ad2.ui.heroGuide.HeroGuideWorker
import n7.ad2.ui.heroInfo.HeroInfoFragment
import n7.ad2.ui.heroInfo.HeroInfoViewModel
import n7.ad2.ui.heroResponse.ResponsesViewModel
import n7.ad2.ui.heroes.HeroesFragment
import n7.ad2.ui.heroes.HeroesViewModel
import n7.ad2.ui.itemInfo.ItemInfoFragment
import n7.ad2.ui.itemInfo.ItemInfoViewModel
import n7.ad2.ui.items.ItemsFragment
import n7.ad2.ui.items.ItemsViewModel
import n7.ad2.ui.streams.StreamViewModel
import javax.inject.Singleton

@Singleton
@dagger.Component(
    modules = [
        ComponentDependenciesModule::class,
        CoroutineModule::class,
        MoshiModule::class,
        RetrofitModule::class,
        NewApplicationModule::class,
        DatabaseModule::class,
    ]
)
interface MainActivityComponent : StreamsDependencies,
    DrawerDependencies,
    DatabaseDependencies {

    fun inject(application: MyApplication)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Application): MainActivityComponent
    }
}

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        CoroutineModule::class,
        MoshiModule::class,
        RetrofitModule::class,
    ]
)
interface ApplicationComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Application): ApplicationComponent
    }

    fun inject(mainActivity: MainActivity)
    fun inject(heroesFragment: HeroesFragment)
    fun inject(itemsFragment: ItemsFragment)
    fun inject(mainActivity2: MainActivity2)
    fun inject(heroInfoFragment: HeroInfoFragment)
    fun inject(heroGuideWorker: HeroGuideWorker)
    fun inject(heroGuideWorker: ItemInfoFragment)

    val tournamentsViewModel: TournamentsViewModel
    val heroesViewModel: HeroesViewModel
    val itemsViewModel: ItemsViewModel
    val responsesViewModel: ResponsesViewModel
    val itemInfoFactory: ItemInfoViewModel.Factory
    val heroInfoFactory: HeroInfoViewModel.Factory
    val heroGuideViewModel: HeroGuideViewModel
    val newsViewModel: NewsViewModel
    val streamViewModel: StreamViewModel

}
