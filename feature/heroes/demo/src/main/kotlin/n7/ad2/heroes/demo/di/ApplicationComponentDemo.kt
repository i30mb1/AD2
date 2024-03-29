package n7.ad2.heroes.demo.di

import android.app.Application
import n7.ad2.coroutines.CoroutineModule
import n7.ad2.dagger.ApplicationScope
import n7.ad2.di.RetrofitModule
import n7.ad2.heroes.demo.MyApplicationDemo
import n7.ad2.heroes.domain.wiring.HeroesModule
import n7.ad2.heroes.ui.api.HeroesDependencies
import n7.ad2.heroes.ui.api.HeroesFragmentFactory

@ApplicationScope
@dagger.Component(
    modules = [
        ApplicationDemoModule::class,
        CoroutineModule::class,
        RetrofitModule::class,
        HeroesModule::class,
    ]
)
internal interface ApplicationComponentDemo : HeroesDependencies {

    val heroesFragmentFactory: HeroesFragmentFactory

    fun inject(myApplicationDemo: MyApplicationDemo)

    @dagger.Component.Factory
    interface Factory {
        fun create(@dagger.BindsInstance applicationContext: Application): ApplicationComponentDemo
    }
}
