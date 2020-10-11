package n7.ad2.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import n7.ad2.ui.heroGuide.HeroGuideViewModel
import n7.ad2.ui.heroGuide.HeroGuideWorker
import n7.ad2.ui.heroInfo.HeroInfoViewModel
import n7.ad2.ui.heroResponse.ResponsesViewModel
import n7.ad2.ui.heroes.HeroesViewModel
import n7.ad2.ui.items.ItemsViewModel
import n7.ad2.ui.splash.SplashViewModel
import n7.ad2.workers.DatabaseWorker
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            ApplicationModule::class,
            AssistedInjectModule::class
        ]
)
interface ApplicationComponent {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance applicationContext: Application): ApplicationComponent
    }

    fun inject(databaseWorker: DatabaseWorker)
    fun inject(heroGuideWorker: HeroGuideWorker)

    val splashViewModel: SplashViewModel
    val heroesViewModel: HeroesViewModel
    val itemsViewModel: ItemsViewModel
    val responsesViewModel: ResponsesViewModel

    val heroInfoViewModelFactory: HeroInfoViewModel.Factory
    val heroGuideViewModelFactory: HeroGuideViewModel.Factory
}
