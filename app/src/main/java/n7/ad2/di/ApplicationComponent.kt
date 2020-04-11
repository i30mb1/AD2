package n7.ad2.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import n7.ad2.ui.splash.SplashViewModel
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            ApplicationModule::class
        ]
)
interface ApplicationComponent {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance applicationContext: Application): ApplicationComponent
    }

    val splashViewModel: SplashViewModel
}
