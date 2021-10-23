package n7.ad2.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import n7.ad2.main.MainViewModel
import n7.ad2.news.NewsViewModel
import n7.ad2.ui.MainActivity
import n7.ad2.ui.heroGuide.HeroGuideViewModel
import n7.ad2.ui.heroGuide.HeroGuideWorker
import n7.ad2.ui.heroInfo.HeroInfoViewModel
import n7.ad2.ui.heroResponse.ResponsesViewModel
import n7.ad2.ui.heroes.HeroesViewModel
import n7.ad2.ui.itemInfo.ItemInfoFragment
import n7.ad2.ui.itemInfo.ItemInfoViewModel
import n7.ad2.ui.items.ItemsViewModel
import n7.ad2.ui.splash.SplashViewModel
import n7.ad2.ui.streams.StreamViewModel
import n7.ad2.ui.streams.StreamsFragment
import n7.ad2.ui.streams.StreamsViewModel
import n7.ad2.workers.DatabaseWorker
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        CoroutineModule::class,
        RetrofitModule::class,
    ]
)
interface ApplicationComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Application): ApplicationComponent
    }

    fun inject(databaseWorker: DatabaseWorker)
    fun inject(mainActivity: MainActivity)
    fun inject(streamsFragment: StreamsFragment)
    fun inject(heroGuideWorker: HeroGuideWorker)
    fun inject(heroGuideWorker: ItemInfoFragment)

    val mainViewModel: MainViewModel
    val splashViewModel: SplashViewModel
    val heroesViewModel: HeroesViewModel
    val itemsViewModel: ItemsViewModel
    val responsesViewModel: ResponsesViewModel
    val itemInfoViewModel: ItemInfoViewModel.Factory
    val heroGuideViewModel: HeroGuideViewModel
    val newsViewModel: NewsViewModel
    val heroInfoViewModel: HeroInfoViewModel
    val streamsViewModel: StreamsViewModel
    val streamViewModel: StreamViewModel

}
